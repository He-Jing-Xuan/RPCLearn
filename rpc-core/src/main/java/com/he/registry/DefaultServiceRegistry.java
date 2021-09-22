package com.he.registry;

import com.he.common.enumeration.RpcError;
import com.he.common.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    @Override
    public synchronized <T> void register(T service) {
        /**
         * 得到 服务提供的名称
         */
        String serviceName=service.getClass().getCanonicalName();
        /**
         * 如果已经包含了该服务就直接返回
         */
        if(registeredService.contains(serviceName))
             return;
        /**
         * 添加服务
         */
        registeredService.add(serviceName);
        /**
         * 得到服务的接口
         */
        Class<?> [] interfaces=service.getClass().getInterfaces();
        /**
         * 如果 服务未实现接口 抛出异常
         */
        if(interfaces.length==0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> i:interfaces){
            serviceMap.put(i.getCanonicalName(),service);
        }
        logger.info("向接口: {} 注册服务: {}", interfaces, serviceName);
    }

    @Override
    public synchronized Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
