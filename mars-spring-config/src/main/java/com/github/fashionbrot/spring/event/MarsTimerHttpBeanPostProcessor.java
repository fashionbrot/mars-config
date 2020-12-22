package com.github.fashionbrot.spring.event;

import com.alibaba.fastjson.JSON;
import com.github.fashionbrot.ribbon.loadbalancer.BaseLoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.ILoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.spring.api.ApiConstant;
import com.github.fashionbrot.spring.api.ForDataVo;
import com.github.fashionbrot.spring.api.ForDataVoList;
import com.github.fashionbrot.spring.config.GlobalMarsProperties;
import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.env.MarsPropertySource;
import com.github.fashionbrot.spring.server.ServerHttpAgent;
import com.github.fashionbrot.spring.support.SourceParseFactory;
import com.github.fashionbrot.spring.util.BeanUtil;
import com.github.fashionbrot.spring.util.FileUtil;
import com.github.fashionbrot.spring.util.ObjectUtils;
import com.github.fashionbrot.ribbon.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author fashionbrot
 * @version 0.1.1
 * @date 2019/12/8 22:45
 */
@Slf4j
public class MarsTimerHttpBeanPostProcessor implements BeanFactoryAware,ApplicationEventPublisherAware,InitializingBean,
        DisposableBean,EnvironmentAware, Ordered {

    public static final String BEAN_NAME="marsTimerHttpBeanPostProcessor";

    private ConfigurableEnvironment environment;

    private ApplicationEventPublisher applicationEventPublisher;

    private BeanFactory beanFactory;

    /**
     * listen long poll ScheduledExecutorService
     */
    private ScheduledExecutorService executorService;


    @Override
    public void afterPropertiesSet() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("MarsTimerHttpBeanPostProcessor afterPropertiesSet ");
        }
        GlobalMarsProperties globalMarsProperties = (GlobalMarsProperties) BeanUtil.getSingleton(beanFactory,GlobalMarsProperties.BEAN_NAME);
        if (globalMarsProperties==null){
            return;
        }
        String appId = globalMarsProperties.getAppName();
        String envCode = globalMarsProperties.getEnvCode();
        long listenLongPollMs = globalMarsProperties.getListenLongPollMs();
        if (ObjectUtils.isEmpty(appId) || ObjectUtils.isEmpty(envCode)) {
            if (log.isInfoEnabled()) {
                log.info(" MarsHttpConfigBeanDefinitionRegistrar init appId is null or envCode is null");
            }
            return;
        }
        String serverAddress = globalMarsProperties.getServerAddress();
        if (StringUtil.isEmpty(serverAddress)) {
            log.warn(" ${mars.config.http.server-address} is null");
            return;
        }


        executorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("com.github.fashionbrot.spring.event.MarsTimerHttpBeanPostProcessor");
                t.setDaemon(true);
                return t;
            }
        });
        ILoadBalancer loadBalancer = new BaseLoadBalancer();
        loadBalancer.setServer(serverAddress,ApiConstant.HEALTH);

        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try{
                    Server server = loadBalancer.chooseServer();
                    if (server==null){
                        log.error("loadBalancer is null  rule:{} ping:{}", loadBalancer.getRule().getClass().getName(), loadBalancer.getPing().getClass().getName());
                        return;
                    }
                    checkForUpdate(server,globalMarsProperties);
                }catch (Throwable e){
                    log.error("MarsTimerHttpBeanPostProcessor longPull error",e);
                }
            }
        },0,listenLongPollMs, TimeUnit.MILLISECONDS);


    }

    public void checkForUpdate(final Server server, GlobalMarsProperties globalMarsProperties) {
        String appId = globalMarsProperties.getAppName();
        String envCode = globalMarsProperties.getEnvCode();

        //判断远程server version 是否比本地version大
        final boolean checkForUpdateVo = ServerHttpAgent.checkForUpdate(server, envCode, appId, false);
        if (!checkForUpdateVo) {

            //如果远程server version 比本地version 大，获取最新的配置文件集合
            final ForDataVoList forData = ServerHttpAgent.getForData(server, envCode, appId, false);

            //写入 environment，并且持久化到 磁盘,使用监听者模式修改  @MarsValue @MarsConfigurationProperties @MarsConfigListener
            // 并且更新最新本地version
            if (forData!=null && CollectionUtil.isNotEmpty(forData.getList())){
                for(ForDataVo vo : forData.getList()){

                    buildMarsPropertySource(server,vo,globalMarsProperties);
                }
                ServerHttpAgent.setLastVersion(envCode,appId,forData.getVersion(),true);
            }
        }
    }


    private  void buildMarsPropertySource(final Server server,ForDataVo dataConfig,GlobalMarsProperties globalMarsProperties){
        String environmentFileName =  ApiConstant.NAME+dataConfig.getFileName();
        String content = dataConfig.getContent();
        String fileName = dataConfig.getFileName();
        String fileType = dataConfig.getFileType();
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        if (mutablePropertySources==null){
            log.error("environment get MutablePropertySources  is null");
            return;
        }
        if (StringUtil.isEmpty(globalMarsProperties.getLocalCachePath())){
            globalMarsProperties.setLocalCachePath(FileUtil.getUserHome(globalMarsProperties.getAppName())) ;
        }
        if(dataConfig.getDelFlag()!=null && dataConfig.getDelFlag()){
            //if del remove env
            removeEnvironment(mutablePropertySources,environmentFileName);
            //remove local file
            ServerHttpAgent.removeSearchFiles(globalMarsProperties.getLocalCachePath(),globalMarsProperties.getAppName(),globalMarsProperties.getEnvCode(),fileName);
            return;
        }

        ConfigTypeEnum configTypeEnum = ConfigTypeEnum.valueTypeOf(dataConfig.getFileType());
        Properties properties = SourceParseFactory.toProperties(dataConfig.getContent(), configTypeEnum);
        if (globalMarsProperties.isEnableLocalCache()){
            //写入本地缓存文件
            ServerHttpAgent.writePathFile(globalMarsProperties.getLocalCachePath(),globalMarsProperties.getAppName(),globalMarsProperties.getEnvCode(),fileName,fileType,content);
        }

        //remove environment
        removeEnvironment(mutablePropertySources, environmentFileName);

        if (properties!=null) {
            MarsPropertySource marsPropertySource = new MarsPropertySource(environmentFileName,properties);
            mutablePropertySources.addLast(marsPropertySource);
        }
        //发送事件
        MarsListenerEvent marsListenerEvent = new MarsListenerEvent(this,fileName );
        applicationEventPublisher.publishEvent(marsListenerEvent);
    }

    private void removeEnvironment(MutablePropertySources mutablePropertySources, String environmentFileName) {
        if (mutablePropertySources.contains(environmentFileName)){
            PropertySource propertySource = mutablePropertySources.remove(environmentFileName);
            if (propertySource!=null){
                if (log.isDebugEnabled()) {
                    log.debug("environment propertySource remove success", environmentFileName);
                }
            }
        }
    }


    @Override
    public void destroy() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("MarsTimerHttpBeanPostProcessor destroy ");
        }
        if (executorService!=null){
            executorService.shutdown();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment  = (ConfigurableEnvironment) environment;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
