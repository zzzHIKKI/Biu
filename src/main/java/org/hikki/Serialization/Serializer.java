package org.hikki.Serialization;

/**
 * Created by HIKKIさまon 2017/11/25 20:45
 * Description:.
 */
public interface Serializer {
    //将对象序列化为字节数组
    byte[] serialize(Object object);
    //将字节数组反序列化为指定类型的对象
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
