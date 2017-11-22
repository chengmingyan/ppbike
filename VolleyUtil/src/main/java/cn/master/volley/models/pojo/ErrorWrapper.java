package cn.master.volley.models.pojo;

/**
 * Created by 一搏 on 2016/2/26.
 */
public class ErrorWrapper extends Wrapper<Object>{
    @Override
    public boolean isSuccess() {
        return code == 200||code == 10000;
    }

    @Override
    public boolean isInvalidUser() {
        return false;
    }
}
