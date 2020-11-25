package com.github.fashionbrot.value.event;

import com.alibaba.fastjson.JSONObject;
import com.github.fashionbrot.ribbon.loadbalancer.BaseLoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.ILoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.value.GlobalMarsValueProperties;
import com.github.fashionbrot.value.HttpService;
import com.github.fashionbrot.value.MarsConfigValue;
import com.github.fashionbrot.value.consts.ApiConsts;
import com.github.fashionbrot.value.model.Resp;
import com.github.fashionbrot.value.util.BeanUtil;
import com.github.fashionbrot.value.util.ObjectUtils;
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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class HttpBeanPostProcessor implements BeanFactoryAware, ApplicationEventPublisherAware, InitializingBean,
        DisposableBean, EnvironmentAware, Ordered {

    public static final String BEAN_NAME="marsValue-HttpBeanPostProcessor";


    private ConfigurableEnvironment environment;

    private ApplicationEventPublisher applicationEventPublisher;

    private BeanFactory beanFactory;

    private GlobalMarsValueProperties properties;

    private AtomicLong version = new AtomicLong(0);
    /**
     * listen long poll ScheduledExecutorService
     */
    private ScheduledExecutorService executorService;

    @Override
    public void afterPropertiesSet()  {
        if (log.isInfoEnabled()) {
            log.info(BEAN_NAME+" afterPropertiesSet ");
        }
        properties = (GlobalMarsValueProperties) BeanUtil.getSingleton(beanFactory,GlobalMarsValueProperties.BEAN_NAME);
        if (properties==null){
            log.error(BEAN_NAME+" config properties is null");
            return;
        }
        executorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("com.github.fashionbrot.value.event.HttpBeanPostProcessor");
                t.setDaemon(true);
                return t;
            }
        });
        ILoadBalancer loadBalancer = new BaseLoadBalancer();
        loadBalancer.setServer(properties.getServerAddress(), ApiConsts.HEALTH);


        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try{
                    Server server = loadBalancer.chooseServer();
                    if (server==null){
                        log.error("loadBalancer server is null  rule:{} ping:{}", loadBalancer.getRule().getClass().getName(), loadBalancer.getPing().getClass().getName());
                        return;
                    }
                    if (!HttpService.checkVersion(server,properties)){
                        HttpService.getData(server,properties,false);
                    }
                }catch (Throwable e){
                    log.error("MarsTimerHttpBeanPostProcessor longPull error",e);
                }
            }
        },0,properties.getListenLongPollMs(), TimeUnit.MILLISECONDS);

    }




    @Override
    public void destroy(){
        if (log.isInfoEnabled()) {
            log.info("HttpBeanPostProcessor destroy ");
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
