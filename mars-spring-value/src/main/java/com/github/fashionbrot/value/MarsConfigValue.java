package com.github.fashionbrot.value;

import com.github.fashionbrot.ribbon.enums.SchemeEnum;
import com.github.fashionbrot.ribbon.loadbalancer.BaseLoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.ILoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.ribbon.util.HttpClientUtil;
import com.github.fashionbrot.ribbon.util.HttpResult;
import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.fashionbrot.value.consts.ApiConsts;
import com.github.fashionbrot.value.model.ConfigValue;
import com.github.fashionbrot.value.util.BeanUtil;
import com.github.fashionbrot.value.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MarsConfigValue implements BeanFactoryAware {

    public static final String BEAN_NAME = "MarsConfigValue";

    private BeanFactory beanFactory;

    private GlobalMarsValueProperties properties;

    private static Map<String,List> cache = new ConcurrentHashMap<>();


    /**
     *  根据 模板获取 模板列表
     * @param templateKey
     * @return
     */
    public static  <E> List<E> getTemplateObject(String templateKey){
        if (cache.containsKey(templateKey)){
            return  cache.get(templateKey);
        }
        return Collections.EMPTY_LIST;
    }


    public static void setCache(List<ConfigValue> data){
        if(CollectionUtil.isNotEmpty(data)){
            for(int i=0;i<data.size();i++){
                ConfigValue value = data.get(i);
                cache.put(value.getTemplateKey(),value.getJsonList());
            }
        }
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        properties = (GlobalMarsValueProperties) BeanUtil.getSingleton(beanFactory,GlobalMarsValueProperties.BEAN_NAME);
    }
}
