package com.github.fashionbrot.spring.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;


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

    public static  <T> T parseObjectNon(String json,Class<T> clazz) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(json)){
            return null;
        }
        return JSONObject.parseObject(json.getBytes(CHARSET), clazz);
    }

}
