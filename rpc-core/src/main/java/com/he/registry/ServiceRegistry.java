package com.he.registry;


public interface ServiceRegistry {
    /**
     * 注册服务
     * @param service
     * @param <T>
     */
    <T> void register(T service);

    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    Object getService(String serviceName);
}
