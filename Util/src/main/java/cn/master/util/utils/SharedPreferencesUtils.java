package cn.master.util.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 在手机中存储用户的一些信息
 * 如：账号、打球设置等
 * 对系统的SharedPreferences进行了封装
 *
 * @author chengmingyan
 */
public class SharedPreferencesUtils {

    private final SharedPreferences settings;

    public SharedPreferencesUtils(Context context, String name) {
        settings = context.getSharedPreferences(name, Context.MODE_APPEND);
    }

    public String getString(String key, String defaultString) {
        return settings.getString(key, defaultString);
    }

    public int getInt(String key, int defaultInt) {
        return settings.getInt(key, defaultInt);
    }

    public boolean getBoolean(String key, boolean defaultBoolean) {
        return settings.getBoolean(key, defaultBoolean);
    }

    public Long getLong(String key, Long defaultLong) {
        return settings.getLong(key, defaultLong);
    }

    public void commitString(String key, String value) {
        settings.edit().putString(key, value).commit();
    }

    public void commitInt(String key, int value) {
        settings.edit().putInt(key, value).commit();
    }

    public void commitBoolean(String key, boolean value) {
        settings.edit().putBoolean(key, value).commit();
    }

    public void commitLong(String key, Long value) {
        settings.edit().putLong(key, value).commit();
    }
}
