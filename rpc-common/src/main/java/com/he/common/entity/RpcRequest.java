package com.he.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 请求号
     */
    private String requestId;


    private String interfaceName;
    /**
     * 待调用方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;
    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;

    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;

}
