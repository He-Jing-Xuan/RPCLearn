package com.he.Proxy;

import com.he.transport.socket.client.SocketClient;
import com.he.common.entity.RpcRequest;
import com.he.common.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class RpcClientProxy implements InvocationHandler {
   private String host;
   private int port;
    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 生成代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }

    /**
     * 来指明代理对象的方法被调用时的动作
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /**
         * 使用Builder生成 request对象
         */
        RpcRequest request=RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())//开始没有设置参数类型而出错。
                .requestId(UUID.randomUUID().toString())
                .build();
        /**
         * 使用RPCclient来发送请求对象。
         */
        SocketClient rpcClient=new SocketClient();
        return ((RpcResponse)rpcClient.sendRequest(request,host,port)).getData();
    }
}
