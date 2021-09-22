package com.he.transport.netty.client;

import com.he.common.entity.RpcRequest;
import com.he.common.entity.RpcResponse;
import com.he.common.enumeration.RpcError;
import com.he.common.exception.RpcException;
import com.he.common.factory.SingletonFactory;
import com.he.loadbalancer.LoadBalancer;
import com.he.loadbalancer.RandomLoadBalancer;
import com.he.registry.nacos.NacosServiceDiscovery;
import com.he.registry.nacos.ServiceDiscovery;
import com.he.serializer.CommonSerializer;
import com.he.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    private final ServiceDiscovery serviceDiscovery;
    private final CommonSerializer serializer;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this(1, new RandomLoadBalancer());
    }
    public NettyClient(LoadBalancer loadBalancer) {
        this(1, loadBalancer);
    }
    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }
    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
        Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
        if (!channel.isActive()) {
            group.shutdownGracefully();
            return null;
        }
        unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
        channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
            } else {
                future1.channel().close();
                resultFuture.completeExceptionally(future1.cause());
                logger.error("发送消息时有错误发生: ", future1.cause());
            }
        });
        return resultFuture;
    }
}
