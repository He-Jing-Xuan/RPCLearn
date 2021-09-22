package com.he.codec;

import com.he.common.entity.RpcRequest;
import com.he.common.entity.RpcResponse;
import com.he.common.enumeration.PackageType;
import com.he.common.enumeration.RpcError;
import com.he.common.exception.RpcException;
import com.he.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 将 序列化的数据 进行反序列化 得到实体对象
 */
public class CommonDecoder extends ReplayingDecoder {
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        /**
         * 获取到请求头的数据
         */
        int magic = in.readInt();
        /**
         * 进行验证 是否符合 自定义协议
         */
        if(magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        /**
         * 得到数据包的 code 来判断 是请求包 还是响应包
         */
        int packageCode = in.readInt();
        /**
         * 通过 code的判断 来对 类 进行初始化。
         */
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if(packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        /**
         * 读取到 序列号器 的编号
         */
        int serializerCode = in.readInt();
        /**
         * 通过编号 得到序列号器
         */
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        /**
         * 得到内容的长度
         */
        int length=in.readInt();
        byte [] result=new byte[length];
        /**
         * 将 序列化 数据写入到 结果数组中
         */
        in.readBytes(result);
        /**
         * 通过解码器 进行反序列化
         */
        Object obj=serializer.deserialize(result,packageClass);
        list.add(obj);

    }
}
