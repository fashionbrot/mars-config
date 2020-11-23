package com.github.fashionbrot.value;

import com.github.fashionbrot.ribbon.enums.SchemeEnum;
import com.github.fashionbrot.ribbon.loadbalancer.BaseLoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.ILoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.util.HttpClientUtil;
import com.github.fashionbrot.ribbon.util.HttpResult;
import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.fashionbrot.value.consts.ApiConsts;
import com.github.fashionbrot.value.util.BeanUtil;
import com.github.fashionbrot.value.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MarsConfigValue implements BeanFactoryAware {

    public static final String BEAN_NAME = "MarsConfigValue";

    private BeanFactory beanFactory;

    private GlobalMarsValueProperties properties;

    public String getAllValue(){
        if (properties==null){
            return null;
        }
        String envCode = properties.getEnvCode();
        String appName = properties.getAppName();
        String serverAddress = properties.getServerAddress();
        if (ObjectUtils.isNotEmpty(envCode) && ObjectUtils.isNotEmpty(appName) && ObjectUtils.isNotEmpty(serverAddress)){
            ILoadBalancer loadBalancer = new BaseLoadBalancer();
            loadBalancer.setServer(serverAddress, ApiConsts.HEALTH);
            Server server = loadBalancer.chooseServer();
            if (server != null) {

            }
        }
        return null;
    }



    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        properties = (GlobalMarsValueProperties) BeanUtil.getSingleton(beanFactory,GlobalMarsValueProperties.BEAN_NAME);
    }
}
