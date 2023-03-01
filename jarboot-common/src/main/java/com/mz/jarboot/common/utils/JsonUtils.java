package com.mz.jarboot.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json序列化、反序列化工具类
 * @author majianzheng
 */
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 解析字符串为 {@link JsonNode}
     * @param content 字符串内容
     * @return JsonNode
     */
    public static JsonNode readAsJsonNode(String content) {
        try {
            return MAPPER.readValue(content, JsonNode.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json反序列化为对象
     * @param content 字符串
     * @param cls 类
     * @param <T> 类型
     * @return 对象
     */
    public static <T> T readValue(String content, Class<T> cls) {
        try {
            return MAPPER.readValue(content, cls);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * json反序列化为对象
     * @param content 字节码
     * @param cls 类
     * @param <T> 类型
     * @return 对象
     */
    public static <T> T readValue(byte[] content, Class<T> cls) {
        try {
            return MAPPER.readValue(content, cls);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json反序列化为对象
     * @param content 字符串
     * @param cls 类
     * @param <T> 类型
     * @return 对象
     */
    public static <T> T treeToValue(JsonNode content, Class<T> cls) {
        try {
            return MAPPER.treeToValue(content, cls);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象序列化为字符串
     * @param obj 对象
     * @return json字符串
     */
    public static String toJsonString(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象序列化为字节码
     * @param obj 对象
     * @return json字节码
     */
    public static byte[] toJsonBytes(Object obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private JsonUtils() {}
}
