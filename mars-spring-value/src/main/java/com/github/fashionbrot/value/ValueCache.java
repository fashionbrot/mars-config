package com.github.fashionbrot.value;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValueCache {

    private static Map<String,List> cache = new ConcurrentHashMap<>();


    /**
     *  根据 模板获取 模板列表
     * @param templateKey
     * @return
     */
    public <E> List<E> getTemplateObject(String templateKey){
        if (cache.containsKey(templateKey)){
            return  cache.get(templateKey);
        }
        return Collections.EMPTY_LIST;
    }


    public void setCache(String templateKey){

    }

}
