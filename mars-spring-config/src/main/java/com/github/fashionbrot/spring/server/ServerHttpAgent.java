package com.github.fashionbrot.spring.server;

import com.alibaba.fastjson.JSON;
import com.github.fashionbrot.ribbon.constants.GlobalConstants;
import com.github.fashionbrot.ribbon.enums.SchemeEnum;
import com.github.fashionbrot.ribbon.loadbalancer.ILoadBalancer;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.ribbon.util.HttpClientUtil;
import com.github.fashionbrot.ribbon.util.HttpResult;
import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.fashionbrot.spring.api.ApiConstant;
import com.github.fashionbrot.spring.api.CheckForUpdateVo;
import com.github.fashionbrot.spring.api.ForDataVo;
import com.github.fashionbrot.spring.config.GlobalMarsProperties;
import com.github.fashionbrot.spring.config.MarsDataConfig;
import com.github.fashionbrot.spring.enums.ApiResultEnum;
import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.env.MarsPropertySource;
import com.github.fashionbrot.spring.exception.MarsException;
import com.github.fashionbrot.spring.util.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import java.io.File;
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
        if ((checkForUpdateVo == null ||  ApiResultEnum.codeOf(checkForUpdateVo.getResultCode()) == ApiResultEnum.FAILED)
            && globalMarsProperties.isEnableLocalCache()) {

            if (StringUtil.isEmpty(globalMarsProperties.getLocalCachePath())){
                log.error("localCachePath is null");
                return;
            }
            String keyWord = ApiConstant.NAME+globalMarsProperties.getAppName()+"_"+globalMarsProperties.getEnvCode();
            List<File> fileList =  FileUtil.searchFiles(new File(globalMarsProperties.getLocalCachePath()),keyWord);
            if (CollectionUtil.isNotEmpty(fileList)){
                for(File file : fileList){
                    String[] fileNames = file.getName().split("_");
                    String context = FileUtil.getFileContent(file);
                    if (StringUtil.isNotEmpty(context)) {

                        buildEnv(environment, globalMarsProperties, fileNames[3], context, ConfigTypeEnum.PROPERTIES.getType());
                    }
                }
            }else{
                log.warn("search path:{} No file found ",globalMarsProperties.getLocalCachePath() );
            }
        }
        if (checkForUpdateVo != null && ApiResultEnum.codeOf(checkForUpdateVo.getResultCode()) == ApiResultEnum.SUCCESS_UPDATE){
            List<String> updateFiles = checkForUpdateVo.getUpdateFiles();
            if (CollectionUtil.isNotEmpty(updateFiles)) {
                for (String file : updateFiles) {

                    MarsDataConfig dataConfig = MarsDataConfig.builder()
                            .envCode(envCode)
                            .appId(appId)
                            .fileName(file)
                            .build();
                    buildMarsPropertySource(server,globalMarsProperties,dataConfig,environment);
                }
            }
        }
    }

    private static void buildMarsPropertySource(final Server server,GlobalMarsProperties globalMarsProperties,MarsDataConfig dataConfig,ConfigurableEnvironment environment){
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

        buildEnv(environment,globalMarsProperties,forDataVo.getFileName(),forDataVo.getContent(),forDataVo.getFileType());
    }

    public static void buildEnv(ConfigurableEnvironment environment,GlobalMarsProperties globalProperties,String fileName,String content,String configType){
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        if (mutablePropertySources==null){
            log.error("environment get property sources is null");
            return;
        }
        String environmentFileName =  ApiConstant.NAME+fileName;
        if (globalProperties!=null) {
            Boolean writeFlag = false;
            if (globalProperties.isEnableLocalCache()){
                if (StringUtil.isEmpty(globalProperties.getLocalCachePath())){
                    log.error("localCachePath is null Please check the configuration  ${mars.config.local-cache-path} ");
                    throw new MarsException("localCachePath is null Please check the configuration  ${mars.config.local-cache-path} ");
                }
                StringBuilder path = new StringBuilder();
                path.append(globalProperties.getLocalCachePath()).append(File.separator).append(ApiConstant.NAME);
                path.append(globalProperties.getAppName()).append("_");
                path.append(globalProperties.getEnvCode()).append("_");
                path.append(fileName);
                FileUtil.writeFile(new File(path.toString()),content);
                writeFlag = true;
            }else{
                writeFlag = true;
            }
            ConfigTypeEnum configTypeEnum = match(fileName);
            Properties properties;
            if (configTypeEnum == ConfigTypeEnum.YAML || configTypeEnum == ConfigTypeEnum.PROPERTIES || configTypeEnum == ConfigTypeEnum.TEXT) {
                properties = PropertiesSourceUtil.toProperties(content, configTypeEnum);
            }else{
                properties = new Properties();
            }
            if (writeFlag!=null && writeFlag.booleanValue()) {
                MarsDataConfig dataConfig = new MarsDataConfig();
                dataConfig.setContent(content);
                dataConfig.setConfigType(ConfigTypeEnum.valueTypeOf(configType));
                MarsPropertySource marsPropertySource = new MarsPropertySource(environmentFileName, properties, dataConfig);
                mutablePropertySources.addLast(marsPropertySource);
            }
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

            List<String> params =new ArrayList<>(6);
            params.add("envCode");
            params.add(env);
            params.add("appId");
            params.add(appId);
            if(StringUtil.isNotEmpty(versions)) {
                params.add("version");
                params.add(versions);
            }
            if (server.getScheme() == SchemeEnum.HTTP) {
                url = String.format(ApiConstant.HTTP_CHECK_FOR_UPDATE_PATH_PARAM, server.getServerIp());
            } else {
                url = String.format(ApiConstant.HTTPS_CHECK_FOR_UPDATE_PATH_PARAM, server.getServerIp());
            }
            HttpResult httpResult  = null;
            try {
                httpResult  = HttpClientUtil.httpPost(url,null,params,GlobalConstants.ENCODE_UTF8,5000,5000);
                if (httpResult.isSuccess()){
                    return JsonUtil.parseObject(httpResult.getContent(),CheckForUpdateVo.class);
                }
                return CheckForUpdateVo.builder().resultCode(ApiResultEnum.FAILED.getResultCode()).build();
            } catch (Exception e) {
                log.error("checkForUpdate error url:{} httpCode:{} error:{}",url,httpResult!=null?httpResult.getCode():"未知",e.getMessage());
                return CheckForUpdateVo.builder().resultCode(ApiResultEnum.FAILED.getResultCode()).build();
            }
        }
        return null;
    }



    private static void throwServerException(Exception e){
        log.error("check-for-update error http message:{}",e.getMessage());
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
