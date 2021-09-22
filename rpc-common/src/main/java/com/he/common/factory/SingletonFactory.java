package com.he.common.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过类命创建对象 并存放到map之中。
 */
public class SingletonFactory {
    private static Map<Class,Object> map=new HashMap<Class, Object>();
    private SingletonFactory() {}
    public static <T> T getInstance(Class<T> clazz) {
        Object instance = map.get(clazz);
        synchronized (clazz){
            try{
                instance=clazz.newInstance();
                map.put(clazz, instance);
            }catch (IllegalAccessException  e){
                throw new RuntimeException(e.getMessage());
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return clazz.cast(instance);
    }
}
