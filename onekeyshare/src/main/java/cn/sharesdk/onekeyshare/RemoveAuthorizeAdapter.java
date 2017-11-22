package cn.sharesdk.onekeyshare;

import cn.sharesdk.framework.authorize.AuthorizeAdapter;

/**
 * Created by chengmingyan on 16/4/7.
 */
public class RemoveAuthorizeAdapter extends AuthorizeAdapter {
    @Override
    public void onCreate() {
        //授权页面标题栏去掉ShareSDK Logo部分
        hideShareSDKLogo();
        super.onCreate();
    }
}
