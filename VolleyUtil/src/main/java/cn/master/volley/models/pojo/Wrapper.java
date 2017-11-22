package cn.master.volley.models.pojo;

/**
 * 网络请求返回数据的包装类
 * Created by 一搏 on 2016/2/25.
 * @param <T> 一个与数据结构匹配的数据实体类，由调用者指定
 */

public abstract class Wrapper<T> {

    @SuppressWarnings("unused")
    private static final String TAG = Wrapper.class.getSimpleName();
    public abstract boolean isSuccess();
    public abstract boolean isInvalidUser();
    /**
     * 状态码
     */
    protected int code;
    /**
     * 返回数据
     */
    protected T data;
    /**
     * 错误信息
     */
    protected String message;

    /**
     * tips : 无效参数, 请检查后重试
     * error : Invalid uuid
     * location : /srv/www/ApiService/releases/20160129072757/app/apis/tournament/v10/tournaments_api.rb:23
     */

    protected String tips;
    protected String error;
    protected String location;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTips() {
        return tips;
    }

    public String getError() {
        return error;
    }

    public String getLocation() {
        return location;
    }
}
