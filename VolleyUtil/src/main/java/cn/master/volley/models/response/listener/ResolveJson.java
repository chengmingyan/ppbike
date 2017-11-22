package cn.master.volley.models.response.listener;


import java.io.IOException;

import cn.master.volley.models.pojo.Wrapper;


/**
 * Json 数据转换接口
 * @param <T> 一个与 {@link Wrapper#getData()} 数据结构匹配的数据实体类，由调用者指定
 */
public interface ResolveJson<T> {
	/**
	 * * 子类需重写该方法，实现json转换Wrapper对象
	 * @param respJson
	 * @return
	 * @throws IOException 
	 */
	Wrapper<T> resolve(String respJson) throws IOException;
}
