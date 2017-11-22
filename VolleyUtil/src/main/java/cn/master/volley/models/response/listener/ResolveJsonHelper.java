package cn.master.volley.models.response.listener;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;

import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.pojo.Wrapper;

/**
 * Json 转换助手类
 */
public class ResolveJsonHelper {
	public static <E extends Wrapper<T>,T> ResolveJson<T> resolveJson2POJO(final Class<E> clazz){
		return new ResolveJson<T>() {
			@Override
			public E resolve(String respJson)
					throws
					IOException {
				return JacksonJsonUtil.getObjectMapper().readValue(respJson, clazz);
			}
		};
	}
}
