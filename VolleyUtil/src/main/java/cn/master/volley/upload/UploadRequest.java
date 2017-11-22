package cn.master.volley.upload;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import cn.master.volley.commons.CustomHeaderRequest;
import cn.master.volley.models.response.listener.IsCacheListener;

/**
 * Sketch Project Studio
 * Created by Angga on 27/04/2016 12.05.
 */
public class UploadRequest extends CustomHeaderRequest {
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();

    public UploadRequest(int method, String url, IsCacheListener<String> listener, Response.ErrorListener errorListener, Context context) {
        super(method, url, listener, errorListener, context);
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // populate text payload
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                textParse(bos, params, getParamsEncoding());
            }

            // populate data byte payload
            Map<String, DataPart> data = getByteData();
            if (data != null && data.size() > 0) {
                dataParse(bos, data, getParamsEncoding());
            }
            /*结尾行*/
            // close multipart form data after text and file data
            String endLine = twoHyphens + boundary + twoHyphens + lineEnd;
            bos.write(endLine.getBytes(getParamsEncoding()));

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return null;
    }

    private void textParse(ByteArrayOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue(), encoding);
            }
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }

    private void dataParse(ByteArrayOutputStream dataOutputStream, Map<String, DataPart> data, String encoding) throws IOException {
        for (Map.Entry<String, DataPart> entry : data.entrySet()) {
            buildDataPart(dataOutputStream, entry.getValue(), entry.getKey(), encoding);
        }
    }

    private void buildTextPart(ByteArrayOutputStream dataOutputStream, String parameterName, String parameterValue, String encoding) throws IOException {
        StringBuffer sb = new StringBuffer();
            /*第一行*/
        sb.append(twoHyphens + boundary + lineEnd);
            /*第二行*/
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"");
        sb.append(parameterName);
        sb.append("\"");
        sb.append(lineEnd);
            /*第三行*/
        sb.append(lineEnd);
            /*第四行*/
        sb.append(parameterValue);
        sb.append(lineEnd);

        dataOutputStream.write(sb.toString().getBytes(encoding));

    }

    private void buildDataPart(ByteArrayOutputStream dataOutputStream, DataPart dataFile, String inputName, String encoding) throws IOException {
        StringBuffer sb = new StringBuffer();
            /*第一行*/
        sb.append(twoHyphens + boundary + lineEnd);
            /*第二行*/
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"");
        sb.append(inputName);
        sb.append("\"; filename=\"");
        sb.append(dataFile.getFileName());
        sb.append("\"");
        sb.append(lineEnd);
            /*第三行*/
        if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
            sb.append("Content-Type: " + dataFile.getType() + lineEnd);
        }
            /*第四行*/
        sb.append(lineEnd);
        dataOutputStream.write(sb.toString().getBytes(encoding));

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.write(lineEnd.getBytes(encoding));
    }
}