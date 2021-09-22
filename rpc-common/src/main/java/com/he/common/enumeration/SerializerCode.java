package com.he.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@AllArgsConstructor

public enum SerializerCode {
    KRYO(0),
    JSON(1);
    private final int code;

}
