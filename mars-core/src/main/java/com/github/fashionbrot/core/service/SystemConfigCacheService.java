package com.github.fashionbrot.core.service;


import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SystemConfigCacheService {

    private Map<String,Long> cache = new ConcurrentHashMap<>();

    public void setCache(String envCode,String appName,Long version){
        setCache(getKey(envCode,appName), version);
    }

    public void setCache(String key,Long version){
        cache.put(key,version);
    }

    public Long getCache(String envCode,String appName){
        return getCache(getKey(envCode,appName));
    }
    public Long getCache(String key){
        if (cache.containsKey(key)){
            return cache.get(key);
        }
        return -1L;
    }

    public boolean containsKey(String envCode,String appName){
        return containsKey(getKey(envCode,appName));
    }

    public boolean containsKey(String key){
        return cache.containsKey(key);
    }

    public String getKey(String envCode,String appName){
        return envCode+appName;
    }
}
