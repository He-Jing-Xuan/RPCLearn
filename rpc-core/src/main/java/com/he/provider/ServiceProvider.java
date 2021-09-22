package com.he.provider;

/**
 * 服务提供的接口
 */
public interface ServiceProvider {
    <T> void addServiceProvider(T service,String serviceName);
    Object getServiceProvider(String serviceName);
}
