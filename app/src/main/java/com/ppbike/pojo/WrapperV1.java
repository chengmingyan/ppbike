package com.ppbike.pojo;

import cn.master.volley.models.pojo.Wrapper;

/**
 * V1接口处理包装类
 * Created by 一搏 on 2016/2/25.
 */
public class WrapperV1<T> extends Wrapper<T> {
    public boolean isSuccess() {
        return code == 10000;
    }

    public boolean isInvalidUser() {
        return code == 20002||code==40401;
    }
}
