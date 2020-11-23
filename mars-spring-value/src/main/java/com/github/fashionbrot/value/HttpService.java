package com.github.fashionbrot.value;

import com.github.fashionbrot.ribbon.enums.SchemeEnum;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.util.HttpClientUtil;
import com.github.fashionbrot.ribbon.util.HttpResult;
import com.github.fashionbrot.value.consts.ApiConsts;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class HttpService {

    private static AtomicLong version = new AtomicLong();

    public static String getData(Server server, GlobalMarsValueProperties dataConfig){
        if (server==null){
            log.warn(" for data server is null");
            return null;
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
                url = String.format(ApiConsts.HTTP_LOAD_DATA, server.getServerIp());
            } else {
                url = String.format(ApiConsts.HTTPS_LOAD_DATA, server.getServerIp());
            }
            try {
                HttpResult httpResult =  HttpClientUtil.httpPost(url,null,params);
                if (httpResult!=null && httpResult.isSuccess()){
                    return httpResult.getContent();
                }
                return null;
            }catch (Exception e) {
                log.error("for-data error  message:{}", e.getMessage());
            }
        }
        return null;
    }


}
