package com.he.transport.socket.server;

import com.he.common.entity.RpcRequest;
import com.he.common.entity.RpcResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 由工作线程 完成请求的执行和结果的响应
 * 接受 RpcRequest对象， 解析并调用方法， 返回结果
 */
public class WorkerThread implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);

    private Socket socket;
    private Object service;
    public WorkerThread(Socket socket,Object service){
        this.service=service;
        this.socket=socket;
    }

    @SneakyThrows
    public void run()  {
        try {


            /**
             * 服务端拿到输入流
             */
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            /**
             * 服务端的输出流
             */
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            /**
             * 通过客户端的输入流 得到RpcRequest
             */
            RpcRequest request = (RpcRequest) objectInputStream.readObject();
            /**
             * 得到请求方法
             */
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            /**
             * 代理模式 执行 请求的方法
             */
            Object returnObject = method.invoke(service, request.getParameters());
            /**
             * 将请求结果返回给 客户端
             */
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        }catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
}
