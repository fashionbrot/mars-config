package com.github.fashionbrot.value.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;


@Slf4j
public class JsonUtil {

    private static final String CHARSET="UTF-8";

    public static  <T> T parseObject(String json,Class<T> clazz){
        if (StringUtils.isEmpty(json)){
            return null;
        }
        T t = null;
        try {
            t= JSONObject.parseObject(json.getBytes(CHARSET), clazz);
        }catch (Exception e){
            log.error("parseObject error json:{} clazz:{}",json,clazz,e);
        }
        return t;
    }


    public static  JSONObject parseObject(String json){
        if (StringUtils.isEmpty(json)){
            return null;
        }
        try {
            return JSONObject.parseObject(json);
        }catch (Exception e){
            log.error("parseObject error json:{}",json,e);
        }
        return null;
    }



}
