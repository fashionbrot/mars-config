package com.fashion.mars.spring.server;

import com.alibaba.fastjson.JSON;
import com.fashion.mars.ribbon.enums.SchemeEnum;
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
import com.fashion.mars.spring.event.MarsListenerEvent;
import com.fashion.mars.spring.util.ConfigParseUtils;
import com.fashion.mars.spring.util.OkHttpUtil;
import com.fashion.mars.spring.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 */
@Slf4j
public class ServerHttpAgent {



    private static final OkHttpUtil OK_HTTP_UTIL = OkHttpUtil.getInstance();

    public static void shutdown(){
        OK_HTTP_UTIL.shutdown();
    }


    public static void checkForUpdate(Server server, GlobalMarsProperties globalMarsProperties, ConfigurableEnvironment environment){
        String appId = globalMarsProperties.getAppName();
        String envCode = globalMarsProperties.getEnvCode();

        CheckForUpdateVo checkForUpdateVo = ServerHttpAgent.checkForUpdate(server, envCode, appId,null);
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
                    buildMarsPropertySource(server,dataConfig,environment);
                }
            }
        }
    }

    private static void buildMarsPropertySource(final Server server,MarsDataConfig dataConfig,ConfigurableEnvironment environment){
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
        ConfigTypeEnum configTypeEnum = match(dataConfig.getFileName());
        Properties properties;
        if (configTypeEnum == ConfigTypeEnum.YAML || configTypeEnum == ConfigTypeEnum.PROPERTIES || configTypeEnum == ConfigTypeEnum.TEXT) {
             properties = ConfigParseUtils.toProperties(forDataVo.getContent(), configTypeEnum);
        }else{
            properties = new Properties();
        }


        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        if (mutablePropertySources==null){
            log.error("environment get property sources is null");
            return;
        }
        String environmentFileName =  ApiConstant.NAME+dataConfig.getFileName();
        if (properties!=null) {
            dataConfig.setContent(forDataVo.getContent());
            dataConfig.setConfigType(configTypeEnum);
            MarsPropertySource marsPropertySource = new MarsPropertySource(environmentFileName,properties,dataConfig);
            mutablePropertySources.addLast(marsPropertySource);
        }
    }


    public static ConfigTypeEnum match (String propertiesName){
        if (StringUtils.isNotEmpty(propertiesName)) {
            if (propertiesName.endsWith(".text")) {
                return ConfigTypeEnum.TEXT;
            }
            if (propertiesName.endsWith(".properties")) {
                return ConfigTypeEnum.PROPERTIES;
            }
            if (propertiesName.endsWith(".yaml")) {
                return ConfigTypeEnum.YAML;
            }
        }
        return ConfigTypeEnum.PROPERTIES;
    }


    public static void setServer(String serverAddress, ILoadBalancer loadBalancer) {
        serverAddress = serverAddress.replaceAll("https://", "").replaceAll("http://", "");
        String[] server = serverAddress.split(",");
        List<Server> serverList = new ArrayList<>(server.length);
        if (server != null && server.length != 0) {
            for (String s : server) {
                String[] svr = s.split(":");
                int port = 80;
                if (svr.length == 2) {
                    port = StringUtil.parseInteger(svr[1], 80);
                }
                serverList.add(Server.builder()
                        .host(svr[0])
                        .scheme(svr[0].startsWith("https") ? SchemeEnum.HTTPS : SchemeEnum.HTTP)
                        .port(port)
                        .path(ApiConstant.HEALTH)
                        .build());
            }
            loadBalancer.addServers(serverList);
        }
    }


    public static CheckForUpdateVo checkForUpdate(Server server, String env, String appId,String versions) {


        String serverAddress =server.getServer();
        if (StringUtils.isNotEmpty(serverAddress)) {
            String url ;

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("envCode",env);
            builder.add("appId",appId);
            if(StringUtil.isNotEmpty(versions)) {
                builder.add("version", versions);
            }

            if (server.getScheme() == SchemeEnum.HTTP) {
                url = String.format(ApiConstant.HTTP_CHECK_FOR_UPDATE_PATH_PARAM, server.getServerIp());
            } else {
                url = String.format(ApiConstant.HTTPS_CHECK_FOR_UPDATE_PATH_PARAM, server.getServerIp());
            }
            CheckForUpdateVo vo =  OK_HTTP_UTIL.requestPost(url,builder, CheckForUpdateVo.class);

            return vo;
        }
        return null;
    }



    private static boolean throwServerException(Exception e){
        log.error("check-for-update error http message:{}",e.getMessage());
        return true;
    }

    public static ForDataVo getData(Server server, MarsDataConfig dataConfig){
        if (server==null){
            log.warn(" for data server is null");
            return null;
        }

        if (dataConfig!=null){
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("envCode",dataConfig.getEnvCode());
            builder.add("appId",dataConfig.getAppId());
            builder.add("fileName",dataConfig.getFileName());

            String url ;
            if (server.getScheme() == SchemeEnum.HTTP) {
                url = String.format(ApiConstant.HTTP_LOAD_DATA, server.getServerIp());
            } else {
                url = String.format(ApiConstant.HTTPS_LOAD_DATA, server.getServerIp());
            }
            try {
                return OK_HTTP_UTIL.requestPost(url,builder,ForDataVo.class);
            }catch (Exception e2) {
                throwServerException(e2);
            }
        }
        return null;
    }


}
