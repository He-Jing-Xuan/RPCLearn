package com.he.registry.nacos;

import java.net.InetSocketAddress;

/**
 * 基于Nacos的服务注册与发现
 */
public interface ServiceRegistry {
    /**
     *
     * @param serviceName 服务名称
     * @param inetSocketAddress 服务地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);


}
