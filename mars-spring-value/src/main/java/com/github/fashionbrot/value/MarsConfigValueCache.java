package com.github.fashionbrot.value;

import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.fashionbrot.value.model.ConfigValue;
import com.github.fashionbrot.value.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public  class MarsConfigValueCache {

    private static Map<String,Class> formatClass=new ConcurrentHashMap<>();

    private static Map<String,List> cache = new ConcurrentHashMap<>();


    /**
     *  根据 模板获取 模板列表
     * @param templateKey
     * @return
     */
    public static  <E> List<E> getTemplateObject(String templateKey){
        if (cache.containsKey(templateKey)){
            return cache.get(templateKey);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 深度 copy cache
     * @param templateKey
     * @param <E>
     * @return
     */
    public static  <E> List<E> getDeepTemplateObject(String templateKey){
        if (cache.containsKey(templateKey)){
            List<E> cacheList = cache.get(templateKey);
            if (CollectionUtil.isNotEmpty(cacheList)) {
                return deepCopy(cacheList);
            }
        }
        return Collections.EMPTY_LIST;
    }

    public static void setCache(List<ConfigValue> data) {

        if (CollectionUtil.isNotEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                ConfigValue value = data.get(i);
                String key = value.getTemplateKey();
                List<String> v = value.getJsonList();
                if (CollectionUtil.isNotEmpty(value.getJsonList())) {
                    if (formatClass.containsKey(key)){
                        Class<?> clazz = formatClass.get(key);
                        List formatList = v.stream().filter(m-> StringUtil.isNotEmpty(m)).map(m-> JsonUtil.parseObject(m,clazz) ).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(formatList)){
                            cache.put(key, formatList);
                        }
                    }else {
                        List formatList = v.stream().filter(m-> StringUtil.isNotEmpty(m)).map(m-> JsonUtil.parseObject(m) ).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(formatList)){
                            cache.put(key, formatList);
                        }
                    }
                }else{
                    cache.put(key, Collections.EMPTY_LIST);
                }
            }
        }
    }


    public static void setFormatClass(Map<String,Class> map){
        if (CollectionUtil.isNotEmpty(map)){
            formatClass.putAll(map);
        }
    }

    public static <T> List<T> deepCopy(List<T> src)  {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (List<T>) in.readObject();
        }catch (Exception e){
            log.error("deepCopy error",e);
        }
        return null;
    }

}
