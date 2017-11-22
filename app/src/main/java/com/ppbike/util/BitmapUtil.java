package com.ppbike.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by chengmingyan on 16/7/7.
 */
public class BitmapUtil {
    public static Bitmap zoomBitmapByWidthAndHeight(String bitmapPath,
                                                    float width, float height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bitmapPath, options);
        float scaleWidth = options.outWidth * 1.0f / width;
        float scaleHeight = options.outHeight * 1.0f / height;
        float scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;
        if (scale < 1.0f) {
            scale = 1.0f;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) scale;
        Bitmap sourceBitmap = BitmapFactory.decodeFile(bitmapPath, options);

        if (sourceBitmap != null) {

            int sourceBitmapWidth = sourceBitmap.getWidth();
            int sourceBitmapHeight = sourceBitmap.getHeight();
            float widthScale = width / sourceBitmapWidth * 1f;
            float heightScale = height / sourceBitmapHeight * 1f;
            scale = widthScale < heightScale ? widthScale : heightScale;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                    sourceBitmapWidth, sourceBitmapHeight, matrix, true);
            if (sourceBitmap != null
                    && (sourceBitmap.getWidth() != bitmap.getWidth())
                    && !sourceBitmap.isRecycled()) {
                sourceBitmap.recycle();
                sourceBitmap = null;
                System.gc();
            }
            return bitmap;
        }else {
            return sourceBitmap;
        }
    }
}
