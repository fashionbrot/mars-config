package com.github.fashionbrot.spring.env;

import com.github.fashionbrot.ribbon.loadbalancer.BaseLoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.ILoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.spring.config.GlobalMarsProperties;
import com.github.fashionbrot.spring.server.ServerHttpAgent;
import com.github.fashionbrot.spring.util.BeanUtil;
import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.fashionbrot.spring.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;


/**
 * @author fashionbrot
 * @version 0.1.1
 * @date 2019/12/8 22:45
 */
@Slf4j
public class MarsPropertySourcePostProcessor implements BeanDefinitionRegistryPostProcessor, BeanFactoryPostProcessor,
        EnvironmentAware, Ordered, DisposableBean {

    public static final String BEAN_NAME = "marsPropertySourcePostProcessor";

    private ConfigurableEnvironment environment;


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {


        GlobalMarsProperties globalMarsProperties = (GlobalMarsProperties) BeanUtil.getSingleton(beanFactory, GlobalMarsProperties.BEAN_NAME);
        if (globalMarsProperties == null) {
            log.warn("globalMarsProperties is null");
            return;
        }
        String appId = globalMarsProperties.getAppName();
        String envCode = globalMarsProperties.getEnvCode();
        if (ObjectUtils.isEmpty(appId) || ObjectUtils.isEmpty(envCode)) {
            if (log.isInfoEnabled()) {
                log.info(" mars init appId is null or envCode is null");
            }
            return;
        }
        String serverAddress = globalMarsProperties.getServerAddress();
        if (StringUtil.isEmpty(serverAddress)) {
            log.warn(" ${mars.config.http.server-address} is null");
            return;
        }
        ILoadBalancer loadBalancer = new BaseLoadBalancer();
        ServerHttpAgent.setServer(serverAddress, loadBalancer);
        loadBalancer.setEnableLog(globalMarsProperties.isEnableErrorLog());
        Server server = loadBalancer.chooseServer();
        if (server == null) {
            ServerHttpAgent.loadLocalConfig(globalMarsProperties,environment);
            return;
        }
        ServerHttpAgent.checkForUpdate(server, globalMarsProperties, environment);

    }


    /**
     * The order is closed to {@link ConfigurationClassPostProcessor#getOrder() HIGHEST_PRECEDENCE} almost.
     *
     * @return <code>Ordered.HIGHEST_PRECEDENCE + 1</code>
     * @see ConfigurationClassPostProcessor#getOrder()
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }


    @Override
    public void destroy() throws Exception {

    }
}