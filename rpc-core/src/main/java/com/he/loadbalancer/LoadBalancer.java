package com.he.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡的接口
 */
public interface LoadBalancer {
    /**
     * 从大量服务中选择 需要的一个
     * @param instances
     * @return
     */
    Instance select(List<Instance> instances);
}
