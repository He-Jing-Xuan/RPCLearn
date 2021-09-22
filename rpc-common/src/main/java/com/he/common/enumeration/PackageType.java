package com.he.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PackageType {
    /**
     * 0 ：表示的是 请求的包
     * 1: 响应的数据包
     */
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
