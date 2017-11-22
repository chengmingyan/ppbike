package com.ppbike.util;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import Decoder.BASE64Encoder;

/**
 * 压缩解压及Base64工具类
 * @author wufuwei
 *
 */
public class ZipUtil {


    /**bitmap
     * zip压缩后进行Base64编码
     * @param bitmap
     * @return
     */
    public static String bitmapZipAndEncode(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        try {
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(GZipUtils.compress(baos.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}