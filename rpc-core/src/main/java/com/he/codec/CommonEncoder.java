package com.he.codec;

import com.he.common.entity.RpcRequest;
import com.he.common.enumeration.PackageType;
import com.he.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 将消息转为字节序列 进行编码
 */
public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(MAGIC_NUMBER);
        //对请求进行编解码
        if(msg instanceof RpcRequest){
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        }else {
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        /**
         * 将 序列号器 的编号 写入通道
         */
        byteBuf.writeInt(serializer.getCode());
        /**
         * 用 对应的序列化器 将msg序列化成byte数组
         */
        byte [] bytes=serializer.serialize(msg);
        /**
         * 将 数据长度写入到通道里
         */
        byteBuf.writeInt(bytes.length);
        /**
         * 最后才将byte数组写入到通道中
         */
        byteBuf.writeBytes(bytes);
    }
}
