package cn.master.volley.commons;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Jackson Json 转换工具类
 */
public class JacksonJsonUtil {

    private static ObjectMapper objectMapper = null;

    /**
     * 获取一个持久化的 ObjectMapper 对象，提高工具转换效率<\br>
     * 转换时忽略本地没有的属性(标签)
     *
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        if (null == objectMapper) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }

    public static String toJson(Object o) throws IOException {
        return getObjectMapper().writeValueAsString(o);
    }
}
