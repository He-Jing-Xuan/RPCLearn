package com.he.handler;

import com.he.common.entity.RpcRequest;
import com.he.provider.ServiceProvider;
import com.he.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;
    static {
        serviceProvider = new ServiceProviderImpl();
    }
    public Object handle(RpcRequest rpcRequest){
        Object service=serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest,service);
    }
    private Object invokeTargetMethod(RpcRequest rpcRequest,Object service){
        Object result = null;
        try{
            Method method=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            result=method.invoke(service,rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
