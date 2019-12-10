package com.gitee.mars.spring.util;

import com.gitee.mars.spring.api.ApiConstant;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
public class OkHttpUtil {

    private static final Map<String,String> HEADER_MAP;

    private static final Headers HEADERS;

    private static final String CHARSET = "UTF-8";

    private static final FormBody.Builder BUILDER = new FormBody.Builder();


    static{
        HEADER_MAP = new HashMap<>();
        HEADER_MAP.put("Client-Version", ApiConstant.VERSION);
        HEADER_MAP.put("Accept-Encoding", "gzip,deflate,sdch");
        HEADER_MAP.put("Connection", "Keep-Alive");

        HEADERS= Headers.of(HEADER_MAP);
    }

    private static OkHttpUtil okHttpUtil;
    private final OkHttpClient okHttpClient;


    private OkHttpUtil() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(3,TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    public void shutdown(){
        if (okHttpClient!=null){
            okHttpClient.dispatcher().executorService().shutdown();
        }
    }

    public static OkHttpUtil getInstance() {
        if (null == okHttpUtil) {
            synchronized (OkHttpUtil.class) {
                if (null == okHttpUtil) {
                    okHttpUtil = new OkHttpUtil();
                }
            }
        }
        return okHttpUtil;
    }

    /**
     * get 请求
     * @param urlString
     * @param callback
     */
    public void get(String urlString, Callback callback) {
        Request request = new Request.Builder().url(urlString).get().build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    /**
     * get 请求
     * @param urlString
     */
    public String  get(String urlString){
        Request request = new Request.Builder()
                .url(urlString)
                .get()
                .headers(HEADERS)
                .header("RequestId", UUID.randomUUID().toString())
                .build();

        String content=  newCall(request);
        if (log.isDebugEnabled()){
            log.debug("get url:{} content:{}",urlString,content);
        }
        return content;
    }

    /**
     * get 请求
     * @param urlString
     */
    public <T> T  get(String urlString,Class<T> tClass) {
        Request request = new Request.Builder()
                .url(urlString)
                .get()
                .headers(HEADERS)
                .header("RequestId", UUID.randomUUID().toString())
                .build();
        String result = newCall(request);
        if (log.isDebugEnabled()){
            log.debug("get url:{} result:{}",urlString,result);
        }
        if (StringUtils.isNotEmpty(result)){
           return JsonUtil.parseObject(result,tClass);
        }
        return null;
    }

    /**
     * post
     * @param urlString
     * @param param
     * @param callback
     */
    public void post(String urlString,Map<String,Object> param, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        if (!CollectionUtils.isEmpty(param)){
            param.forEach((k,v)->{
                if (!Objects.isNull(v)) {
                    builder.add(k, v.toString());
                }
            });
        }
        Request request = new Request.Builder().url(urlString).method("POST", builder.build()).build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    /**
     *  post 请求
     * @param urlString
     * @param param
     * @return
     */
    public <T> T requestpost(String urlString,Map<String,Object> param,Class<T> tClass)  {
        FormBody.Builder builder = new FormBody.Builder();
        if (!CollectionUtils.isEmpty(param)){
            param.forEach((k,v)->{
                if (!Objects.isNull(v)) {
                    builder.add(k, v.toString());
                }
            });
        }
        return requestPost(urlString,builder,tClass);
    }



    /**
     * request post
     * @param urlString
     * @param builder
     * @param tClass
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T requestPost(String urlString,FormBody.Builder builder,Class<T> tClass) {

        String json = requestPost(urlString,builder);
        if (!StringUtils.isEmpty(json)){
            if (log.isDebugEnabled()){
                log.debug("post url:{} result:{}",urlString,json);
            }
            return JsonUtil.parseObject(json,tClass);
        }
        return null;
    }


    public String requestPost(String urlString,FormBody.Builder builder)  {

        Request request = new Request.Builder()
                .url(urlString)
                .method("POST",builder!=null?builder.build():BUILDER.build())
                .headers(HEADERS)
                .header("RequestId", UUID.randomUUID().toString())
                .build();
        return newCall(request);
    }


    @Nullable
    private String newCall(Request request) {
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response!=null){
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ResponseBody responseBody = response.body();
                    try {
                        if (responseBody!=null) {
                            return new String(responseBody.bytes(), CHARSET);
                        }
                    } finally {
                        if (responseBody != null) {
                            responseBody.close();
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("newCall Unsupported Encoding error",e);
        } catch (IOException e) {
            log.error("newCall IOException error",e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }


    //添加拦截器
    static class LoggingInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            return chain.proceed(request);
        }
    }



    public static void main(String[] args) {
         OkHttpUtil okHttpUtil1 = OkHttpUtil.getInstance();
        long start=System.currentTimeMillis();

        Map<String,Object> map =new HashMap<>();
        map.put("appid","youliwang");
        map.put("timestamp",System.currentTimeMillis()/1000);
        String result = okHttpUtil1.requestPost("https://baidu.com",null);
        System.out.println("result:"+result );
        System.out.println(System.currentTimeMillis()-start);

    }

}
