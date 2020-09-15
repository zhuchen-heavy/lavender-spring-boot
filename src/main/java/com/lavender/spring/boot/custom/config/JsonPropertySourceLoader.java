package com.lavender.spring.boot.custom.config;

import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 *  扩展点：自定义PropertySourceLoader。SpringBoot默认仅支持".properties"和".yml"文件的加载解析。
 *  功能：自定义".json"文件的配置解析，并将其配置到 /META-INF/spring.factories 中
 *  {@link org.springframework.boot.context.config.ConfigFileApplicationListener}会通过 SpringFactoriesLoader#loadFactories 加载 PropertySourceLoader的 实现。
 *
 * 通过 spring.config.additional-location 指定".json"配置文件，即可自定义".json"文件的 名字 和 位置， e.g "/conf/a.json"
 * 若不通过 spring.config.additional-location 指定，则 spring boot的resource默认的名字为 application，e.g "application.json"
 *
 * TODO 不明白为什么打开 spring.factories 中的配置后（自定义PropertySourceLoader后），spring boot控制台的日志会丢失。
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/9/15
 * @version: v1.0.0
 */
public class JsonPropertySourceLoader implements PropertySourceLoader {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"json"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        // 将 json 转换为 map
        Map<String, Object> result = mapPropertySource(resource);
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new OriginTrackedMapPropertySource(name, Collections.unmodifiableMap(result), true));
    }

    private Map<String, Object> mapPropertySource(Resource resource) throws IOException {
        if (resource == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = parser.parseMap(readFile(resource));
        nestMap("", result, map);
        return result;
    }

    private String readFile(Resource resource) throws IOException {
        InputStream inputStream = resource.getInputStream();
        List<Byte> byteList = new LinkedList<Byte>();
        byte[] readByte = new byte[1024];
        int length;
        while ((length = inputStream.read(readByte)) > 0) {
            for (int i = 0; i < length; i++) {
                byteList.add(readByte[i]);
            }
        }
        byte[] allBytes = new byte[byteList.size()];
        int index = 0;
        for (Byte soloByte : byteList) {
            allBytes[index] = soloByte;
            index += 1;
        }
        return new String(allBytes);
    }

    private void nestMap(String prefix, Map<String, Object> result, Map<String, Object> map) {
        if (prefix.length() > 0) {
            prefix += ".";
        }
        for (Map.Entry entrySet : map.entrySet()) {
            if (entrySet.getValue() instanceof Map) {
                nestMap(prefix + entrySet.getKey(), result, (Map<String, Object>) entrySet.getValue());
            } else {
                result.put(prefix + entrySet.getKey().toString(), entrySet.getValue());
            }
        }
    }

}
