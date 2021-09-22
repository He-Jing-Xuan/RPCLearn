package com.he.transport.socket.server;

import com.he.common.entity.RpcRequest;
import com.he.common.entity.RpcResponse;
import com.he.handler.RequestHandler;
import com.he.serializer.CommonSerializer;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 处理客户端请求的一个线程
 */
public class SocketRequestHandlerThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);
    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;
    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }



    @SneakyThrows
    @Override
    public void run() {
        try{
            /**
             * 获取到客户端请求的输入流
             */
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            /**
             * 获取到 服务的 输出流， 用于对处理结果的返回
             */
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            /**
             * 在输入流中 得到请求对象
             */
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            /**
             *  得到rpcRequest 的接口名称
             */
          //  String interfaceName = rpcRequest.getInterfaceName();
            /**
             * 通过接口名称 在注册中心 找到 对应的服务
             */
        //    Object service=serviceRegistry.getService(interfaceName);
            /**
             * 找到服务之后 在handler中 去 执行正在的逻辑
             */
            Object result=requestHandler.handle(rpcRequest);
            /**
             * 将输出结果 返回给 客户端
             */
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        }catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        }


    }
}
