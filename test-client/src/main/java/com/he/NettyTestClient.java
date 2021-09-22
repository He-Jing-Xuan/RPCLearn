package com.he;

import com.he.annotation.ServiceScan;
import com.he.api.HelloObject;
import com.he.api.HelloService;
import com.he.serializer.CommonSerializer;
import com.he.transport.RpcClient;
import com.he.transport.RpcClientProxy;
import com.he.transport.netty.client.NettyClient;

public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client=new NettyClient(CommonSerializer.JSON_SERIALIZER);
        RpcClientProxy rpcClientProxy=new RpcClientProxy(client);
        HelloService helloService=rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }


}
