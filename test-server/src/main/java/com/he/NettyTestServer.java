package com.he;

import com.he.annotation.ServiceScan;
import com.he.serializer.CommonSerializer;
import com.he.transport.RpcServer;
import com.he.transport.netty.server.NettyServer;

@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.JSON_SERIALIZER);
        server.start();
    }
}
