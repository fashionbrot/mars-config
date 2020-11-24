package com.github.fashionbrot.value;

import com.alibaba.fastjson.JSONObject;
import com.github.fashionbrot.ribbon.enums.SchemeEnum;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.util.HttpClientUtil;
import com.github.fashionbrot.ribbon.util.HttpResult;
import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.fashionbrot.value.consts.ApiConsts;
import com.github.fashionbrot.value.model.Resp;
import com.github.fashionbrot.value.util.JsonUtil;
import com.github.fashionbrot.value.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class HttpService {

    private static AtomicLong version = new AtomicLong();

    public static boolean checkVersion(Server server ,GlobalMarsValueProperties dataConfig){
        if (server==null){
            log.warn(" for data server is null");
            return true;
        }

        if (dataConfig!=null){

            List<String> params =new ArrayList<>(6);
            params.add("envCode");
            params.add(dataConfig.getEnvCode());
            params.add("appId");
            params.add(dataConfig.getAppName());

            params.add("version");
            params.add(version.longValue()+"");


            String url ;
            if (server.getScheme() == SchemeEnum.HTTP) {
                url = String.format(ApiConsts.HTTP_CHECK_VERSION, server.getServerIp());
            } else {
                url = String.format(ApiConsts.HTTPS_CHECK_VERSION, server.getServerIp());
            }
            try {
                HttpResult httpResult =  HttpClientUtil.httpPost(url,null,params);
                if (httpResult!=null && httpResult.isSuccess()){
                    if (StringUtil.isNotEmpty(httpResult.getContent())) {
                        JSONObject jsonObject = JSONObject.parseObject(httpResult.getContent());
                        if (jsonObject!=null && jsonObject.containsKey("data")){
                            Long v = jsonObject.getLong("data");
                            if (v!=null && v==0){
                                return true;
                            }
                            if (version.get() < v.longValue()){
                                return false;
                            }
                        }
                    }
                }
            }catch (Exception e) {
                log.error("for-data error  message:{}", e.getMessage());
            }
        }

        return true;
    }

    public static void getData(Server server, GlobalMarsValueProperties dataConfig){
        if (server==null){
            log.warn(" for data server is null");
            return ;
        }

        if (dataConfig!=null){

            List<String> params =new ArrayList<>(4);
            params.add("envCode");
            params.add(dataConfig.getEnvCode());
            params.add("appId");
            params.add(dataConfig.getAppName());

            /*params.add("version");
            params.add(version.longValue()+"");*/


            String url ;
            if (server.getScheme() == SchemeEnum.HTTP) {
                url = String.format(ApiConsts.HTTP_LOAD_DATA, server.getServerIp());
            } else {
                url = String.format(ApiConsts.HTTPS_LOAD_DATA, server.getServerIp());
            }
            try {
                HttpResult httpResult =  HttpClientUtil.httpPost(url,null,params);
                if (httpResult!=null && httpResult.isSuccess() && ObjectUtils.isNotEmpty(httpResult.getContent())){
                    Resp resp = JsonUtil.parseObject(httpResult.getContent(),Resp.class);
                    if (resp!=null && resp.isSuccess()){
                        MarsConfigValue.setCache(resp.getData());
                        if (resp.getVersion()!=null) {
                            version.set(resp.getVersion());
                        }
                    }
                }
            }catch (Exception e) {
                log.error("for-data error  message:{}", e.getMessage());
            }
        }
    }


}