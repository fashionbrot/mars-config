package com.github.fashionbrot.spring.event;

import com.alibaba.fastjson.JSON;
import com.github.fashionbrot.ribbon.loadbalancer.BaseLoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.ILoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.spring.api.ApiConstant;
import com.github.fashionbrot.spring.api.CheckForUpdateVo;
import com.github.fashionbrot.spring.api.ForDataVo;
import com.github.fashionbrot.spring.config.GlobalMarsProperties;
import com.github.fashionbrot.spring.config.MarsDataConfig;
import com.github.fashionbrot.spring.enums.ApiResultEnum;
import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.env.MarsPropertySource;
import com.github.fashionbrot.spring.server.ServerHttpAgent;
import com.github.fashionbrot.spring.util.BeanUtil;
import com.github.fashionbrot.spring.util.ObjectUtils;
import com.github.fashionbrot.spring.util.PropertiesSourceUtil;
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
import org.springframework.util.CollectionUtils;

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
        ServerHttpAgent.setServer(serverAddress, loadBalancer);


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

    public  void checkForUpdate(Server server, GlobalMarsProperties globalMarsProperties){
        String appId = globalMarsProperties.getAppName();
        String envCode = globalMarsProperties.getEnvCode();

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
        if (ObjectUtils.isEmpty(forDataVo.getContent())) {
            if (log.isDebugEnabled()) {
                log.debug(" triggerEvent  content is null dataConfig:{} ", JSON.toJSONString(dataConfig));
            }
            return;
        }
        ConfigTypeEnum configTypeEnum = ServerHttpAgent.match(dataConfig.getFileName());
        Properties properties = PropertiesSourceUtil.toProperties(forDataVo.getContent(), configTypeEnum);

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
