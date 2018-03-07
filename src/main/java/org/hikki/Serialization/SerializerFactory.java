package org.hikki.Serialization;

import java.util.ServiceLoader;

/**
 * Created by HIKKIさまon 2017/11/25 20:45
 * Description:.
 */
public class SerializerFactory {

    public static Serializer load(){
        return ServiceLoader.load(Serializer.class).iterator().next();
    }
}
