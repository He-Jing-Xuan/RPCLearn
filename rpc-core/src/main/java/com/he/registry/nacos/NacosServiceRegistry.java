package com.he.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.he.common.enumeration.RpcError;
import com.he.common.exception.RpcException;
import com.he.common.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.jca.GetInstance;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger= LoggerFactory.getLogger(NacosServiceRegistry.class);
   // private static final String SERVER_ADDR = "127.0.0.1:8848";
   // private static final NamingService namingService;
//    static {
//        try {
//            namingService = NamingFactory.createNamingService(SERVER_ADDR);
//        } catch (NacosException e) {
//            logger.error("连接到Nacos时有错误发生: ", e);
//            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
//        }
//    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName,inetSocketAddress);
        }catch (NacosException e){
            logger.error("注册服务时有错误发生:", e);
            e.printStackTrace();
        }
    }



}
