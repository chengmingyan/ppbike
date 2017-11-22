package com.ppbike.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.ppbike.R;

import java.io.File;

import cn.master.util.utils.FileUtil;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * Created by chengmingyan on 8/4/15.
 */
public class ShareSDKUtil {
    private static final String DEFAULT_IMAGE = "http://augusta.oss-cn-beijing.aliyuncs.com/0c/a60502c9296c6299e125fc9963ce17da.png";

    public static void showShare(Context context, String title, String content, String imageUrl, String webUrl) {
        showShare(context, null, title, content, imageUrl, webUrl);
    }

    public static void showShare(final Context context, View v, String title, String content, String imageUrl, String webUrl) {

        OnekeyShare oks = new OnekeyShare();
        //在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        // 是否直接分享
        oks.setSilent(false);
        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        // 令编辑页面显示为Dialog模式
        oks.setDialogMode();

//        boolean isExist = WXAPIFactory.createWXAPI(context, Constants.APP_ID).isWXAppInstalled();
//        if (!isExist) {
//            oks.addHiddenPlatform("Wechat");
//            oks.addHiddenPlatform("WechatMoments");
//            oks.addHiddenPlatform("WechatFavorite");
//        }

        // 必传:title标题、text分享文本、url、titleUrl
        if (title == null)
            title = context.getString(R.string.app_name);
        oks.setTitle(title);

        if (content == null)
            content = "";
        oks.setText(content);

        oks.setUrl(webUrl);
        oks.setTitleUrl(webUrl);
        // //QZone分享完之后返回应用时提示框上显示的名称
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(webUrl);

        //image，必传，QQ、微信
        if (imageUrl == null) {
            imageUrl = DEFAULT_IMAGE;
        }
        if (imageUrl.contains("http")) {
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            oks.setImageUrl(imageUrl);
        } else {
            if (isNumeric(imageUrl)) {
                try {
                    int id = Integer.valueOf(imageUrl).intValue();
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
                    imageUrl = getResourceImagePath(context, id);
                    if (!new File(imageUrl).exists()) {
                        FileUtil.saveBitmap2File(bitmap, new File(imageUrl));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            oks.setImagePath(imageUrl);//确保SDcard下面存在此张图片
        }

        oks.setShareContentCustomizeCallback(new ShareContentCustomize(context, title, content, imageUrl, webUrl));
// 启动分享GUI
        oks.show(context);
        try {
            Log.e("shareData=", title + "," + oks.getText() + "," + imageUrl + "," + webUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showShareImage(final Context context, String title, String content, String imageUrl) {
        if (imageUrl == null || "".equals(imageUrl))
            return;
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        if (title == null)
            title = context.getString(R.string.app_name);
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ/QQ空间使用，注意在此处添加信息，可能导致qq图片分享打不开
        oks.setTitleUrl(imageUrl);
        // text是分享文本，所有平台都需要这个字段
        if (content == null)
            content = "分享";
        oks.setText(content);

        if (imageUrl.contains("http")) {
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            oks.setImageUrl(imageUrl);
        } else {
            oks.addHiddenPlatform("SinaWeibo");
            oks.addHiddenPlatform("QQ");
            oks.addHiddenPlatform("QZone");
            oks.addHiddenPlatform("TencentWeibo");
            oks.setImagePath(imageUrl);//确保SDcard下面存在此张图片
        }
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(imageUrl);
        oks.setDialogMode();
        // 启动分享GUI
        oks.show(context);

        try {
            Log.e("shareData=", title + "," + oks.getText() + "," + imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /*
    * 获取完整路径
    */
    private static String getResourceImagePath(Context context, int imageId) {
        File file = null;
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            file = new File(context.getExternalCacheDir(), imageId + ".jpg");
        } else {
            file = new File(context.getCacheDir(), imageId + ".jpg");
        }
        return file.getAbsolutePath();
    }


    public static class ShareContentCustomize implements ShareContentCustomizeCallback {
        private String title, content, imageUrl, webUrl;
        private Context context;

        public ShareContentCustomize(Context context, String title, String content, String imageUrl, String webUrl) {
            this.context = context;
            this.title = title;
            this.content = content;
            this.imageUrl = imageUrl;
            this.webUrl = webUrl;
        }

        @Override
        public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
            switch (platform.getName()) {
                case "SinaWeibo":
                    paramsToShare.setText(title + webUrl);
                    break;
            }

        }
    }
}
