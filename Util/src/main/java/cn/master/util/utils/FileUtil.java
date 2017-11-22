package cn.master.util.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import cn.master.util.R;

import static android.content.Context.MODE_APPEND;

public class FileUtil {
    /**
     * 该类负责对文件所有的操作
     */
    public final int FINISHSAVEPHOTO = 0;
    public final int ALREADYEXIST = -1;
    public final int FAILEDTOSAVEPHOTO = 1;
    private final Context context;

    public File getOSAlbumFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/GolfLive" + System.currentTimeMillis() + ".jpg");
    }

    public File getOSALbumPath() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera");

    }

    public FileUtil(Context context) {
        this.context = context;
    }

    Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case FINISHSAVEPHOTO:
                    Toast.makeText(context, R.string.finishsavephoto, Toast.LENGTH_SHORT).show();

                    break;
                case ALREADYEXIST:
                    Toast.makeText(context, R.string.alreadyexist, Toast.LENGTH_SHORT).show();
                    break;
                case FAILEDTOSAVEPHOTO:
                    Toast.makeText(context, R.string.failedtosavephoto, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 对文件进行读取
     * 返回二进制数组
     */
    public byte[] readFile(String fileName) throws Exception {
        FileInputStream fis = context.openFileInput(fileName);
        byte buffer[] = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        while ((len = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        fis.close();
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 将信息写到文件中去
     * 手机内存中
     */
    public void save(String fileName, String content) throws Exception {
        //私有
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(content.getBytes());
        fos.close();
    }

    /**
     * 将信息写到sd卡中
     */
    public void saveMemory(String fileName, String content) throws Exception {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //在存储卡上建立fileName的文件
            File f = new File(Environment.getExternalStorageDirectory(), fileName);
            //如果文件不存在 则自动创建 所以不用再文件不存在的情况下自己手动调用创建
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(content.getBytes());
            fos.close();
        }
    }

    /**
     * 将信息追加到手机内存中的文件中
     */
    public void saveAppend(String fileName, String content) throws Exception {
        FileOutputStream fos = context.openFileOutput(fileName, MODE_APPEND);
        fos.write(content.getBytes());
        fos.close();
    }

    /**
     * 将信息追加到sd卡中
     */
    public void saveMemoryAppend(String fileName, String content) throws Exception {
        File f = new File(Environment.getExternalStorageDirectory(), fileName);
        FileOutputStream fos = new FileOutputStream(f);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.append(content);
        bw.close();
        fos.close();
    }

    /**
     * 获取手机中文件输入流
     */
    public InputStream getPhoneInputStream(String fileName) throws Exception {
        File f = new File(fileName);
        InputStream is = new FileInputStream(f);
        return is;
    }

    /**
     * 获取输入流
     */
    public InputStream getInputStream(String fileName) throws Exception {
        File f = new File(Environment.getExternalStorageDirectory(), fileName);
        InputStream is = new FileInputStream(f);
        return is;
    }

    /**
     * 将输入流转变成字节数组
     */
    public byte[] convertByte(InputStream in) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[100];
        int len;
        while ((len = in.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return bos.toByteArray();
    }

    /**
     * 将文件读为2进制数组
     *
     * @param file
     * @return 返回文件的2进制数组
     * @throws IOException
     */
    public static byte[] getHex2FromFile(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        byte[] inputTemp = new byte[input.available()];
        input.read(inputTemp);
        input.close();
        return inputTemp;
    }

    /**
     * 将二进制数组存储到文件
     *
     * @param hex2     二进制字符数组
     * @param filePath 要存储文件的路径
     * @throws IOException
     */
    public static void writeHex2ToFile(byte[] hex2, String filePath)
            throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        InputStream input = new ByteArrayInputStream(hex2);
        FileOutputStream output = new FileOutputStream(filePath);
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = input.read(b)) != -1) {
            output.write(b, 0, len);
        }
        output.flush();
        output.close();
        input.close();

    }

    /**
     * 从网络上下载图片到本地，并存储在文件下
     *
     * @param url          网络地址
     * @param photoPathUrl 本地路径
     */
    public static synchronized boolean downloadAerialPhoto(String url, String photoPathUrl) {
        boolean flag = false;
        // String1 photoHost = "http://192.168.1.220:8080/res/img/aerialPhoto/";
        if (!url.contains("http") || photoPathUrl == null) {
            return flag;
        }
        Log.i("PhotoPath", url);
        // httpGet连接对象
        HttpGet httpRequest = new HttpGet(url);
        // 取得HttpClient 对象
        HttpClient httpclient = new DefaultHttpClient();
        try {
            // 请求httpClient ，取得HttpRestponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 取得相关信息 取得HttpEntiy
                HttpEntity httpEntity = httpResponse.getEntity();
                // 获得一个输入流
                InputStream is = httpEntity.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                FileOutputStream bos = new FileOutputStream(photoPathUrl);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bitmap.isRecycled();
                bos.flush();
                bos.close();
                is.close();
                flag = true;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @SuppressWarnings("unused")
    public void downFile(String url, String path, String filename)
            throws IOException {
        // 下载函数
        // filename = url.substring(url.lastIndexOf("/") + 1);
        // 获取文件名
        URL myURL = new URL(url);
        URLConnection conn = myURL.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        int fileSize = conn.getContentLength();// 根据响应获取文件大小
        if (fileSize <= 0)
            throw new RuntimeException(" unknow file size ");
        if (is == null)
            throw new RuntimeException(" inputstream is null ");
        FileOutputStream fos = new FileOutputStream(path + filename);
        // 把数据存入路径+文件名
        byte buf[] = new byte[1024];
        int downloadFileSize = 0;
        // sendData(0);
        do {
            // 循环读取
            int numread = is.read(buf);
            if (numread == -1) {
                break;
            }
            fos.write(buf, 0, numread);
            // downloadFileSize += numread;
            downloadFileSize += numread;

            // sendData(1);// 更新进度条
        } while (true);
        // sendData(2);// 通知下载完成
        if (is != null) {
            is.close();
        }
        if (fos != null) {
            fos.close();
        }

    }

    /**
     * 判断SD卡是否存在
     */
    public static boolean isExternalStorageState() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static boolean copyFileToSD(Context context, String fromPath,
                                       String toPath) {
        boolean flag = false;
        InputStream myInput;
        OutputStream myOutput;
        try {
            myOutput = new FileOutputStream(toPath);
            myInput = context.getAssets().open(fromPath);
            byte[] buffer = new byte[myInput.available()];
            int count = 0;
            while ((count = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, count);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /*
     * 写在/mnt/sdcard/目录下面的文件
     */
    public static void writeFileSdcard(File fileName, String message) {

        try {

            // FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);

            FileOutputStream fout = new FileOutputStream(fileName);

            byte[] bytes = message.getBytes();

            fout.write(bytes);

            fout.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    /*
     *  读在/mnt/sdcard/目录下面的文件
     */
    public static String readFileSdcard(File fileName) {

        String res = "";

        try {

            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];

            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return res;

    }

    /**
     * 删除文件夹所有内容
     */
    public static void deleteFile(File file) {

        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            //
        }
    }

    /**
     * 删除文件夹所有内容
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            //
        }
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long 单位为M
     * @throws Exception
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        return size / 1048576;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param filepath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath)
            throws IOException {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);

            if (file.isDirectory()) {// 处理目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFolderFile(files[i].getAbsolutePath(), true);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {// 如果是文件，删除
                    file.delete();
                } else {// 目录
                    if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * 保存bitmap到文件
     *
     * @param ph
     * @param file
     */
    public void saveBitmapToFile(final Bitmap ph, final File file) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {

                    if (!file.exists()) {
                        new File(file.getParent()).mkdirs();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(file));
                    ph.compress((file.getName().endsWith(".png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG), 100, bos);
                    bos.flush();
                    bos.close();
                    ph.recycle();

                    handler.sendEmptyMessage(FINISHSAVEPHOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(FAILEDTOSAVEPHOTO);
                }
            }
        }).start();
    }

    public void saveFileToFile(final File file) {
        String filename = file.getParentFile().getName() + "_" + file.getName();
        final File newfile = new File(getOSALbumPath(), filename);

        if (newfile.exists()) {
            handler.sendEmptyMessage(ALREADYEXIST);
        } else {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {

                        if (!newfile.exists()) {
                            newfile.getParentFile().mkdirs();
                        }
                        FileOutputStream bos =
                                new FileOutputStream(newfile);
                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[fis.available()];
                        fis.read(buffer);
                        bos.write(buffer);
                        bos.flush();
                        bos.close();
                        Uri localUri = Uri.fromFile(newfile);

                        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);

                        context.sendBroadcast(localIntent);

                        handler.sendEmptyMessage(FINISHSAVEPHOTO);
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(FAILEDTOSAVEPHOTO);
                    }
                }
            }).start();
        }
    }

    /**
     * 指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param filepath
     * @return
     */
    public static boolean existFolderFile(String filePath, String fileName)
            throws IOException {
        boolean exist = false;
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);

            if (file.isDirectory()) {// 处理目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    existFolderFile(files[i].getAbsolutePath(), fileName);
                }
            }
            if (!file.isDirectory()) {// 如果是文件，删除
                if (file.exists() && file.getName().equals(fileName)) {
                    exist = true;
                }
            }
        }
        return exist;
    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    /*
     * 获取网络图片存储的完整路径
     */
    public static String getNetWorkPhotoFullPath(Context context, String url) {
        File file = null;
        String[] path = url.split("/");
        String photoName = path[path.length - 1];
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            file = new File(context.getExternalCacheDir(), photoName);
        } else {
            file = new File(context.getCacheDir(), photoName);
        }
        return file.getAbsolutePath();
    }

    /*
     * 获取缓存图片存储的完整路径
     */
    public static String getCachePhotoFullPath(Context context, String photoName) {
        File file = null;
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            file = new File(context.getExternalCacheDir(), photoName);
        } else {
            file = new File(context.getCacheDir(), photoName);
        }
        return file.getAbsolutePath();
    }

    /**
     * 保存bitmap到文件
     *
     * @param ph
     * @param file
     */
    public static void saveBitmap2File(final Bitmap ph, final File file) {
        try {

            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            ph.compress((file.getName().endsWith(".png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG), 100, bos);
            bos.flush();
            bos.close();
            ph.recycle();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendString2File(final String ph, final File file, Context c) {
        try {

            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
            }
            FileOutputStream bos = new FileOutputStream(file);
            bos.write(ph.getBytes());
            bos.flush();
            bos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
