package org.hikki.utils;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by HIKKIさまon 2017/11/25 19:25
 * Description:.
 */
public class PropertyReader {

    public static String getPropetty(String path, String key){
        URL url = Resources.getResource(path);
        ByteSource byteSource = Resources.asByteSource(url);
        Properties properties = new Properties();
        InputStream inputStream = null;
        String value = null;
        try {
            inputStream = byteSource.openBufferedStream();
            properties.load(inputStream);
            value = properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
