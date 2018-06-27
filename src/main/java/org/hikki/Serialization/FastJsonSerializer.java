package org.hikki.Serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by HIKKIさまon 2017/11/25 20:47
 * Description:.
 */
public class FastJsonSerializer implements Serializer {
    public byte[] serialize(Object object) {
        byte[] bytes = JSON.toJSONBytes(object, SerializerFeature.SortField);
        return bytes;
    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        T result = JSON.parseObject(bytes,clazz, Feature.SortFeidFastMatch);
        return result;
    }
}
