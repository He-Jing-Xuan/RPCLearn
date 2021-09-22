package com.he.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.he.common.entity.RpcRequest;
import com.he.common.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 序列化
     * @param obj
     * @return
     */
    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 反序列化:根据字节数组和Class 反序列化成对象
     * @param bytes
     * @param clazz
     * @return
     */
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try{
            Object obj=objectMapper.readValue(bytes,clazz);
            if(obj instanceof RpcRequest)
                obj=handlerRequest(obj);
            return obj;
        }catch (IOException e){
            logger.error("反序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 由于 rpcRequest 的其中一个字段是Object数组，在反序列化时序列化器会根据字段类型进行反e序列化
     * 但是由于Object是个模糊的类型，会出现反序列化失败的问题。
     * 这时 就利用RPCRequest的另一个字段  ParamTypes  来获取Object数组对应的类来复制进行反序列化。
     * @param obj
     * @return
     * @throws IOException
     */
    private Object handlerRequest(Object obj) throws IOException {
        RpcRequest request=(RpcRequest)obj;
        for(int i=0;i<request.getParamTypes().length;i++){
            //得到实例的类
            Class<?> clazz=request.getParamTypes()[i];
            // 如果不是同一个类 则会从新进行序列化
            if(!clazz.isAssignableFrom(request.getParameters()[i].getClass())){
                byte[] bytes = objectMapper.writeValueAsBytes(request.getParameters()[i]);
                request.getParameters()[i]=objectMapper.readValue(bytes,clazz);
            }
        }
        return request;
    }

    @Override
    public int getCode() {

      return  SerializerCode.valueOf("JSON").getCode();
    }
}
