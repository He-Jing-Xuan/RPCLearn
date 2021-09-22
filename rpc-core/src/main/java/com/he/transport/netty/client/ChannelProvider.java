package com.he.transport.netty.client;

import com.he.codec.CommonDecoder;
import com.he.codec.CommonEncoder;
import com.he.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.java2d.cmm.lcms.LcmsServiceProvider;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
     private static EventLoopGroup eventLoopGroup;
     private static Map<String, Channel> channelMap=new ConcurrentHashMap<>();
     private static Bootstrap bootstrap =initializeBootstrap();
     public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer commonSerializer){
         String key=inetSocketAddress.toString()+commonSerializer.getCode();
         if(channelMap.containsKey(key)){
             Channel channel=channelMap.get(key);
             if(channel!=null&&channel.isActive()){
                 return channel;
             }else {
                 channelMap.remove(key);
             }
         }
         bootstrap.handler((new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel ch) throws Exception {
                 /*自定义序列化编解码器*/
                 // RpcResponse -> ByteBuf
                 ch.pipeline().addLast(new CommonEncoder(commonSerializer))
                         .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                         .addLast(new CommonDecoder())
                         .addLast(new NettyClientHandler());
             }
         }));
         Channel channel=null;
         try{
             channel=connect(bootstrap,inetSocketAddress);
         }catch (Exception e){
             e.printStackTrace();
             logger.error("连接客户端时有错误发生", e);
             return null;
         }
         channelMap.put(key,channel);
         return channel;
     }
     private static Channel connect(Bootstrap bootstrap,InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
         CompletableFuture<Channel> completableFuture=new CompletableFuture<>();
         bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener)future->{
             if(future.isSuccess()){
                 logger.info("客户端连接成功!");
                 completableFuture.complete(future.channel());
             }else {
                 throw new IllegalStateException();
             }
         });
         return completableFuture.get();
     }
     private static Bootstrap initializeBootstrap(){
         eventLoopGroup=new NioEventLoopGroup();
         Bootstrap bootstrap=new Bootstrap();
         bootstrap.group(eventLoopGroup)
                 .channel(NioSocketChannel.class)
                 //设置连接超时时间
                 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                 //是否开启TCP 心跳
                 .option(ChannelOption.SO_KEEPALIVE,true)
                 //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输
                 .option(ChannelOption.TCP_NODELAY, true);
         return bootstrap;
     }
}

