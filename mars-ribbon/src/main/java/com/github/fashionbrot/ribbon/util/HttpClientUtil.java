package com.github.fashionbrot.ribbon.util;

import lombok.ToString;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public class HttpClientUtil {

    public static final String ENCODE = "UTF-8";

    static public HttpResult httpGet(String url, List<String> headers, List<String> paramValues,
                                     String encoding, long readTimeoutMs, boolean isSSL,int connectTimeout) throws Exception {
        String encodedContent = encodingParams(paramValues, encoding);
        url += (null == encodedContent) ? "" : ("?" + encodedContent);

        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout((int)readTimeoutMs);

            setHeaders(conn, headers, encoding);
            conn.connect();

            int respCode = conn.getResponseCode();
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            if (inputStream!=null){
                resp = toString(inputStream, encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (inputStream!=null){
                inputStream.close();
            }
        }
    }

    /**
     * 发送GET请求。
     */
    static public HttpResult httpGet(String url, List<String> headers, List<String> paramValues, String encoding,
                                     long readTimeoutMs,int connectTimeout) throws Exception {
        return httpGet(url, headers, paramValues, encoding, readTimeoutMs, false,connectTimeout);
    }

    /**
     * 发送POST请求。
     *
     * @param url
     * @param headers       请求Header，可以为null
     * @param paramValues   参数，可以为null
     * @param encoding      URL编码使用的字符集
     * @param readTimeoutMs 响应超时
     * @param isSSL         是否https
     * @return
     * @throws IOException
     */
    static public HttpResult httpPost(String url, List<String> headers, List<String> paramValues,
                                      String encoding, long readTimeoutMs, boolean isSSL,int connectTimeout) throws Exception {
        String encodedContent = encodingParams(paramValues, encoding);

        HttpURLConnection conn = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout((int)readTimeoutMs);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            setHeaders(conn, headers, encoding);

            outputStream = conn.getOutputStream();
            if (outputStream!=null) {
                outputStream.write(encodedContent!=null?encodedContent.getBytes(encoding):"?1=1".getBytes());
                outputStream.flush();
            }
            int respCode = conn.getResponseCode();
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {
                inputStream =  conn.getInputStream();
                resp = toString(conn.getInputStream(), encoding);
            } else {
                inputStream =  conn.getErrorStream();
            }
            if (inputStream!= null) {
                resp = toString(inputStream, encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
            if (inputStream!=null){
                inputStream.close();
            }
            if (outputStream!=null){
                outputStream.close();
            }
        }
    }

    /**
     * 发送POST请求。
     *
     * @param url
     * @param headers       请求Header，可以为null
     * @param paramValues   参数，可以为null
     * @param encoding      URL编码使用的字符集
     * @param readTimeoutMs 响应超时
     * @return
     * @throws IOException
     */
    static public HttpResult httpPost(String url, List<String> headers, List<String> paramValues, String encoding,
                                      long readTimeoutMs,int connectTimeout) throws Exception {
        return httpPost(url, headers, paramValues, encoding, readTimeoutMs, false,connectTimeout);
    }

    static private void setHeaders(HttpURLConnection conn, List<String> headers, String encoding) {
        if (null != headers) {
            for (Iterator<String> iter = headers.iterator(); iter.hasNext(); ) {
                conn.addRequestProperty(iter.next(), iter.next());
            }
        }
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);
    }

    private static List<String> getHeaders( List<String> headers) {
        List<String> newHeaders = new ArrayList<String>();
        if (headers != null) {
            newHeaders.addAll(headers);
        }
        return newHeaders;
    }

    static private String encodingParams(List<String> paramValues, String encoding)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == paramValues) {
            return null;
        }

        for (Iterator<String> iter = paramValues.iterator(); iter.hasNext(); ) {
            sb.append(iter.next()).append("=");
            sb.append(URLEncoder.encode(iter.next(), encoding));
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }


    static public String toString(InputStream input, String encoding) throws Exception {
        return (null == encoding) ? toString(new InputStreamReader(input, ENCODE))
                : toString(new InputStreamReader(input, encoding));
    }
    static public String toString(Reader reader) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        long copy = copy(reader, sw);
        return sw.toString();
    }

    static public long copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[1 << 12];
        long count = 0;
        for (int n = 0; (n = input.read(buffer)) >= 0; ) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    @ToString
    static public class HttpResult {
        final public int code;
        final public String content;

        public boolean isSuccess(){
            return 200 == this.code;
        }
        HttpResult(int code, String content) {
            this.code = code;
            this.content = content;
        }
    }

}
