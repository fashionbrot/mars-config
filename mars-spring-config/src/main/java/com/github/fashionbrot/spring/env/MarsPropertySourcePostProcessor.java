package com.github.fashionbrot.spring.env;

import com.github.fashionbrot.ribbon.loadbalancer.BaseLoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.ILoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.spring.api.ApiConstant;
import com.github.fashionbrot.spring.api.ForDataVo;
import com.github.fashionbrot.spring.api.ForDataVoList;
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
        loadBalancer.setServer(serverAddress, ApiConstant.HEALTH);

        Server server = loadBalancer.chooseServer();
        if (server == null) {
            ServerHttpAgent.loadLocalConfig(globalMarsProperties,environment);
            return;
        }

        /**
         * 判断远程server version 是否比本地version大
         */
        /*boolean checkForUpdateVo = ServerHttpAgent.checkForUpdate(server, envCode, appId,true);
        if (checkForUpdateVo){
            //如果version返回 0 直接加载本地磁盘 配置文件
            ServerHttpAgent.loadLocalConfig(globalMarsProperties,environment);
        }else{
            //如果远程server version 比本地version 大，获取最新的配置文件集合
            ForDataVoList forDataVo = ServerHttpAgent.getForData(server,envCode,appId,true);
            //写入 environment，并且持久化到 磁盘, 并且更新最新本地version
            ServerHttpAgent.saveRemoteResponse(environment,globalMarsProperties,forDataVo);
        }*/
        //如果远程server version 比本地version 大，获取最新的配置文件集合
        ForDataVoList forDataVo = ServerHttpAgent.getForData(server,envCode,appId,true);
        //写入 environment，并且持久化到 磁盘, 并且更新最新本地version
        ServerHttpAgent.saveRemoteResponse(environment,globalMarsProperties,forDataVo);

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