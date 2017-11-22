// Copyright (C) 2012-2013 UUZZ All rights reserved
package com.ppbike.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** 
 * 类 名: GZipUtils<br/>
 * 描 述: gzip解压缩工具类<br/>
 * 作 者: 费玖玲<br/>
 * 创 建： 2013-6-20<br/>
 *
 * 历 史: (版本) 作者 时间 注释 <br/>
 */
public abstract class GZipUtils {
	
	/**
	 * 常量数据字节缓冲大小
	 */
	public static final int BUFFER = 1024;
  
    /**
     * 描 述：数据压缩<br/>
     * 作 者：费玖玲<br/>
     * 历 史: (版本) 作者 时间 注释 <br/>
     * @param data 未压缩数据
     * @return 压缩数据
     * @throws Exception
     */
    public static byte[] compress(byte[] data) throws Exception {
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        byte[] output = null;
		try {
			bais = new ByteArrayInputStream(data);  
			baos = new ByteArrayOutputStream();  
	        // 压缩  
	        compress(bais, baos);  
	  
	        output = baos.toByteArray();  
	  
	        baos.flush();  
	  
		} finally {
			if (baos != null) {
		        baos.close();  
			}
			if (bais != null) {
		        bais.close();  
			}
		}
        return output;  
    }
  
    /**
     * 描 述：数据压缩<br/>
     * 作 者：费玖玲<br/>
     * 历 史: (版本) 作者 时间 注释 <br/>
     * @param is 输入流
     * @param os 输出流
     * @throws Exception
     */
    public static void compress(InputStream is, OutputStream os)  
            throws Exception {  
    	GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(os);  
	        
	        int count;  
	        byte[] data = new byte[BUFFER];  
	        while ((count = is.read(data, 0, BUFFER)) != -1) {  
	            gos.write(data, 0, count);  
	        }  
	  
	        gos.finish();  
	  
	        gos.flush();  
		} finally {
			if (gos != null) {
		        gos.close();  
			}
		}
    }
  
    /**
     * 描 述：数据解压缩 <br/>
     * 作 者：费玖玲<br/>
     * 历 史: (版本) 作者 时间 注释 <br/>
     * @param data 压缩数据
     * @return 解压后数据
     * @throws Exception
     */
    public static byte[] decompress(byte[] data) throws Exception {
    	if(data==null || data.length==0)
    		return data;
    	byte[] output = null;
    	ByteArrayInputStream bais = null;
    	ByteArrayOutputStream baos = null;
		try {
			bais = new ByteArrayInputStream(data);  
			baos = new ByteArrayOutputStream();  
	        // 解压缩  
			  
	        decompress(bais, baos);  
	  
	        output = baos.toByteArray();  
	  
	        baos.flush();  
		} finally {
			if (baos != null) {
		        baos.close();  
			}
			if (bais != null) {
		        bais.close();  
			}
		}
        return output;  
    }
  
    /**
     * 描 述：数据解压缩 <br/>
     * 作 者：费玖玲<br/>
     * 历 史: (版本) 作者 时间 注释 <br/>
     * @param is 输入流
     * @param os 输出流
     * @throws Exception
     */
    public static void decompress(InputStream is, OutputStream os)  
            throws Exception {
    	GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(is); 
	        int count;  
	        byte[] data = new byte[BUFFER];  
	        while ((count = gis.read(data, 0, BUFFER)) != -1) {  
	            os.write(data, 0, count);  
	        }  
		} finally {
			if (gis != null) {
		        gis.close();  
			}
		}
    }
}