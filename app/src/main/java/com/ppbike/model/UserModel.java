package com.ppbike.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ppbike.bean.LoginResult;
import com.ppbike.bean.PerfectInfomationRequest;
import com.ppbike.bean.UserBean;

import java.io.IOException;

import cn.master.volley.commons.JacksonJsonUtil;

/**
 * Created by chengmingyan on 16/6/16.
 */
public class UserModel {

    private static UserModel instance;
    private Context context;
    private static final String SHAREDPREFERENTS_NAME = "user_sharedpreferents";
    private static final String USER = "user";
    private UserBean userBean;

    public UserModel(Context context) {
        this.context = context.getApplicationContext();
    }

    public static UserModel getInstance(Context context){
        if (instance == null){
            instance = new UserModel(context);
        }
        return instance;
    }

    public void saveUserInfomation(UserBean bean) {
        SharedPreferences sharePreferences = context.getSharedPreferences(SHAREDPREFERENTS_NAME,Context.MODE_PRIVATE);
        String userJson = sharePreferences.getString(USER,null);
        if (TextUtils.isEmpty(userJson)){
            userBean = new UserBean();
        }else{
            try {
                userBean = JacksonJsonUtil.getObjectMapper().readValue(userJson,UserBean.class);
            } catch (IOException e) {
                e.printStackTrace();
                userBean = new UserBean();
            }
        }
        userBean.setToken(bean.getToken());
        userBean.setDstatus(bean.getDstatus());
        try {
            userJson = JacksonJsonUtil.toJson(userBean);
            sharePreferences.edit().putString(USER,userJson).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLogin(LoginResult bean,String phone) {
        SharedPreferences sharePreferences = context.getSharedPreferences(SHAREDPREFERENTS_NAME,Context.MODE_PRIVATE);
        String userJson = sharePreferences.getString(USER,null);
        if (TextUtils.isEmpty(userJson)){
            userBean = new UserBean();
        }else{
            try {
                userBean = JacksonJsonUtil.getObjectMapper().readValue(userJson,UserBean.class);
            } catch (IOException e) {
                e.printStackTrace();
                userBean = new UserBean();
            }
        }
        userBean.setToken(bean.getToken());
        userBean.setDstatus(bean.getDstatus());
        userBean.setMcode(bean.getMcode());
        userBean.setNick(bean.getNick());
        userBean.setPhone(phone);
        try {
            userJson = JacksonJsonUtil.toJson(userBean);
            sharePreferences.edit().putString(USER,userJson).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePerfectionInfomation(PerfectInfomationRequest bean) {
        SharedPreferences sharePreferences = context.getSharedPreferences(SHAREDPREFERENTS_NAME,Context.MODE_PRIVATE);
        String userJson = sharePreferences.getString(USER,null);
        if (TextUtils.isEmpty(userJson)){
            userBean = new UserBean();
        }else{
            try {
                userBean = JacksonJsonUtil.getObjectMapper().readValue(userJson,UserBean.class);
            } catch (IOException e) {
                e.printStackTrace();
                userBean = new UserBean();
            }
        }
        if (!TextUtils.isEmpty(bean.getName()))
            userBean.setName(bean.getName());
        if (!TextUtils.isEmpty(bean.getNick()))
            userBean.setNick(bean.getNick());
        if (TextUtils.isEmpty(bean.getIcard()))
            userBean.setDstatus(1);
        else{
            userBean.setDstatus(2);
            userBean.setIcard(bean.getIcard());
        }
        try {
            userJson = JacksonJsonUtil.toJson(userBean);
            sharePreferences.edit().putString(USER,userJson).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserBean getUserBean() {
        if (userBean == null){
            SharedPreferences sharePreferences = context.getSharedPreferences(SHAREDPREFERENTS_NAME,Context.MODE_PRIVATE);
            String userJson = sharePreferences.getString(USER,null);
            if (TextUtils.isEmpty(userJson)){
                userBean = new UserBean();
            }else{
                try {
                    userBean = JacksonJsonUtil.getObjectMapper().readValue(userJson,UserBean.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    userBean = new UserBean();
                }
            }
        }
        return userBean;
    }

    public boolean isLogin(){
        if (TextUtils.isEmpty(getUserBean().getToken())){
            return false;
        }
        return true;
    }
    public void loginout() {
        SharedPreferences sharePreferences = context.getSharedPreferences(SHAREDPREFERENTS_NAME,Context.MODE_PRIVATE);
        sharePreferences.edit().putString(USER,"").commit();
        userBean = null;
    }
}
