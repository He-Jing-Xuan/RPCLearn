package com.he.transport.netty.server;

import com.he.common.entity.RpcRequest;
import com.he.common.entity.RpcResponse;
import com.he.handler.RequestHandler;
import com.he.registry.DefaultServiceRegistry;
import com.he.registry.ServiceRegistry;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行真正的方法调用
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    public NettyServerHandler(ServiceRegistry serviceRegistry){
        this.serviceRegistry=serviceRegistry;
    }
    //类加载时 进行初始化
    static {
        requestHandler=new RequestHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try{
            logger.info("服务器接收到请求: {}", msg);
            String interfaceName=msg.getInterfaceName();

            //注册中心中得到服务
            Object service=serviceRegistry.getService(interfaceName);
            //执行 方法的调用
            Object result=requestHandler.handle(msg);
            //将执行结果 返回给调用方
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

}
