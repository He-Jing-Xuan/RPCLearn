package com.he.transport;

import com.he.common.entity.RpcRequest;
import com.he.serializer.CommonSerializer;

public interface RpcClient {
    /**
     * 客户端发送请求的共用接口
     * @param rpcRequest
     * @return
     */
    Object sendRequest(RpcRequest rpcRequest);

    /**
     * 默认的序列化方式
     */
    int DEFAULT_SERIALIZER = CommonSerializer.JSON_SERIALIZER;
}
