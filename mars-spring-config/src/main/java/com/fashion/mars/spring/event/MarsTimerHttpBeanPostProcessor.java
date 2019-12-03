package com.fashion.mars.spring.event;

import com.alibaba.fastjson.JSON;
import com.fashion.mars.ribbon.loadbalancer.BaseLoadBalancer;
import com.fashion.mars.ribbon.loadbalancer.ILoadBalancer;
import com.fashion.mars.ribbon.loadbalancer.Server;
import com.fashion.mars.spring.api.ApiConstant;
import com.fashion.mars.spring.api.CheckForUpdateVo;
import com.fashion.mars.spring.api.ForDataVo;
import com.fashion.mars.spring.config.GlobalMarsProperties;
import com.fashion.mars.spring.config.MarsDataConfig;
import com.fashion.mars.spring.enums.ApiResultEnum;
import com.fashion.mars.spring.enums.ConfigTypeEnum;
import com.fashion.mars.spring.env.MarsPropertySource;
import com.fashion.mars.spring.server.ServerHttpAgent;
import com.fashion.mars.spring.util.BeanUtil;
import com.fashion.mars.spring.util.ConfigParseUtils;
import com.fashion.mars.spring.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;

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

    private Map<String,String> versionMap = new ConcurrentHashMap<>();

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
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(envCode)) {
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
                t.setName("com.fashion.mars.spring.event.MarsTimerHttpBeanPostProcessor");
                t.setDaemon(true);
                return t;
            }
        });
        ILoadBalancer loadBalancer = new BaseLoadBalancer();
        ServerHttpAgent.setServer(serverAddress, loadBalancer);


        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try{
                    Server server = loadBalancer.chooseServer();
                    if (server==null){
                        log.error("loadBalancer is null  rule:{} ping:{}",loadBalancer.getRule(),loadBalancer.getPing());
                        return;
                    }
                    checkForUpdate(server,globalMarsProperties);
                }catch (Throwable e){
                    log.error("MarsTimerHttpBeanPostProcessor longPull error",e);
                }
            }
        },0,listenLongPollMs, TimeUnit.MILLISECONDS);


    }

    public  void checkForUpdate(Server server, GlobalMarsProperties globalMarsProperties){
        String appId = globalMarsProperties.getAppName();
        String envCode = globalMarsProperties.getEnvCode();
        if (globalMarsProperties.isListenLongPollLogEnabled()){
            log.info("mars long pull server:{} appId:{} envCode:{}",server.getServerIp(),appId,envCode );
        }
        String versions = getVersion();

        CheckForUpdateVo checkForUpdateVo = ServerHttpAgent.checkForUpdate(server, envCode, appId,versions);
        if (checkForUpdateVo != null &&
                ApiResultEnum.codeOf(checkForUpdateVo.getResultCode()) == ApiResultEnum.SUCCESS_UPDATE) {

            List<String> updateFiles = checkForUpdateVo.getUpdateFiles();
            if (!CollectionUtils.isEmpty(updateFiles)) {
                for (String file : updateFiles) {

                    MarsDataConfig dataConfig = MarsDataConfig.builder()
                            .envCode(envCode)
                            .appId(appId)
                            .fileName(file)
                            .build();
                    buildMarsPropertySource(server,dataConfig);
                    versionMap.put(file,dataConfig.getVersion());
                }
            }
        }
    }

    private String getVersion(){
        if (!CollectionUtils.isEmpty(versionMap)){
            String versions =null;
            for(Map.Entry<String,String > m: versionMap.entrySet()){
                if (versions==null){
                    versions= m.getValue();
                }else{
                    versions = versions.concat(",").concat(m.getValue());
                }
            }
            return versions;
        }
        return null;
    }

    private  void buildMarsPropertySource(final Server server,MarsDataConfig dataConfig){
        ForDataVo forDataVo = ServerHttpAgent.getData(server, dataConfig);

        if (forDataVo == null) {
            if (log.isDebugEnabled()) {
                log.debug(" triggerEvent  content is null dataConfig:{} ", JSON.toJSONString(dataConfig));
            }
            return;
        }
        if (StringUtils.isEmpty(forDataVo.getContent())) {
            if (log.isDebugEnabled()) {
                log.debug(" triggerEvent  content is null dataConfig:{} ", JSON.toJSONString(dataConfig));
            }
            return;
        }
        ConfigTypeEnum configTypeEnum = ServerHttpAgent.match(dataConfig.getFileName());
        Properties properties =ConfigParseUtils.toProperties(forDataVo.getContent(), configTypeEnum);

        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        if (mutablePropertySources==null){
            log.error("environment get MutablePropertySources  is null");
            return;
        }
        String environmentFileName =  ApiConstant.NAME+dataConfig.getFileName();
        if (mutablePropertySources.contains(environmentFileName)){
            PropertySource propertySource = mutablePropertySources.remove(environmentFileName);
            if (propertySource!=null){
                if (log.isDebugEnabled()) {
                    log.debug("environment propertySource remove success", environmentFileName);
                }
            }
        }
        if (properties!=null) {
            dataConfig.setContent(forDataVo.getContent());
            dataConfig.setConfigType(configTypeEnum);
            dataConfig.setVersion(forDataVo.getVersion());
            MarsPropertySource marsPropertySource = new MarsPropertySource(environmentFileName,properties,dataConfig);
            mutablePropertySources.addLast(marsPropertySource);
        }
        MarsListenerEvent marsListenerEvent = new MarsListenerEvent(this, forDataVo.getContent(), dataConfig);
        applicationEventPublisher.publishEvent(marsListenerEvent);
    }


    @Override
    public void destroy() throws Exception {
        if (log.isInfoEnabled()) {
            log.info("MarsTimerHttpBeanPostProcessor destroy ");
        }
        if (executorService!=null){
            executorService.shutdown();
        }
        ServerHttpAgent.shutdown();
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
