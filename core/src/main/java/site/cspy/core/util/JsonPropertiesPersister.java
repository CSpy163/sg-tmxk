package site.cspy.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.input.ReaderInputStream;
import org.springframework.lang.NonNull;
import org.springframework.util.PropertiesPersister;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



public class JsonPropertiesPersister implements PropertiesPersister {

    private static final ObjectMapper OM = new ObjectMapper();

    @Override
    public void load(@NonNull Properties props, @NonNull InputStream is) throws IOException {
        Map<String, Object> originMap = OM.readValue(is, new TypeReference<>() {
        });
        // 将嵌套结构平铺
        Map<String, String> resultMap = new HashMap<>();
        flattenMap("", originMap, resultMap);
        props.putAll(resultMap);
    }

    // 递归方法，用于将嵌套结构平铺
    private static void flattenMap(String parentKey, Map<String, Object> originMap, Map<String, String> resultMap) {
        for (Map.Entry<String, Object> entry : originMap.entrySet()) {
            String key = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // 递归处理子 Map
                flattenMap(key, (Map<String, Object>) value, resultMap);
            } else {
                // 处理普通键值对
                resultMap.put(key, value.toString());
            }
        }
    }

    @Override
    public void load(@NonNull Properties props, @NonNull Reader reader) throws IOException {
        InputStream inputStream = ReaderInputStream.builder().setReader(reader).get();
        load(props, inputStream);
    }

    @Override
    public void store(@NonNull Properties props, @NonNull OutputStream os, @NonNull String header) throws IOException {

    }

    @Override
    public void store(@NonNull Properties props, @NonNull Writer writer, @NonNull String header) throws IOException {

    }

    @Override
    public void loadFromXml(@NonNull Properties props, @NonNull InputStream is) throws IOException {

    }

    @Override
    public void storeToXml(@NonNull Properties props, @NonNull OutputStream os, @NonNull String header) throws IOException {

    }

    @Override
    public void storeToXml(@NonNull Properties props, @NonNull OutputStream os, @NonNull String header, @NonNull String encoding) throws IOException {

    }
}
