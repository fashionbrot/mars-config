package com.github.fashionbrot.spring.server;

import com.github.fashionbrot.ribbon.constants.GlobalConstants;
import com.github.fashionbrot.ribbon.enums.SchemeEnum;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.ribbon.util.HttpClientUtil;
import com.github.fashionbrot.ribbon.util.HttpResult;
import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.fashionbrot.spring.api.ApiConstant;
import com.github.fashionbrot.spring.api.ForDataVo;
import com.github.fashionbrot.spring.api.ForDataVoList;
import com.github.fashionbrot.spring.config.GlobalMarsProperties;
import com.github.fashionbrot.spring.config.MarsDataConfig;
import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.env.MarsPropertySource;
import com.github.fashionbrot.spring.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fashionbrot
 * @version 0.1.1
 * @date 2019/12/8 22:45
 *
 */
@Slf4j
public class ServerHttpAgent {

    private static Map<String,Long> lastVersion =new ConcurrentHashMap<>();

    public static void loadLocalConfig(GlobalMarsProperties globalMarsProperties, ConfigurableEnvironment environment){
        String appId = globalMarsProperties.getAppName();
        if (StringUtil.isEmpty(globalMarsProperties.getLocalCachePath())){
            globalMarsProperties.setLocalCachePath(FileUtil.getUserHome(appId));
        }
        String keyWord = ApiConstant.NAME+globalMarsProperties.getAppName()+"_"+globalMarsProperties.getEnvCode();
        List<File> fileList =  FileUtil.searchFiles(new File(globalMarsProperties.getLocalCachePath()),keyWord);
        if (CollectionUtil.isNotEmpty(fileList)){
            for(File file : fileList){
                String[] fileNames = file.getName().split("_");
                String context = FileUtil.getFileContent(file);
                if (StringUtil.isNotEmpty(context)) {
                    buildEnv(environment, globalMarsProperties, fileNames[3], context, fileNames[4]);
                }
            }
        }else{
            log.warn("search path:{} No file found ",globalMarsProperties.getLocalCachePath() );
        }
    }



    public static void saveRemoteResponse(ConfigurableEnvironment environment,
                                GlobalMarsProperties globalProperties,
                                ForDataVoList dataVo){
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        if (mutablePropertySources==null){
            log.error("environment get property sources is null");
            return;
        }
        if (dataVo!=null && CollectionUtil.isNotEmpty(dataVo.getList())){
            for(ForDataVo vo :dataVo.getList()){
                buildEnvironmentAndWriteDisk(globalProperties,vo,mutablePropertySources);
            }
            setLastVersion(globalProperties.getEnvCode(),globalProperties.getAppName(),dataVo.getVersion());
        }
    }

    public static void setLastVersion(String envCode,String appName,Long version){
        String key = getKey(envCode,appName);
        lastVersion.put(key,version);
    }

    public static void setLastVersion(String envCode,String appName,Long version,boolean first){
        String key = getKey(envCode,appName);
        if (first){
            Long clientVersion = lastVersion.get(key);
            if (log.isDebugEnabled()){
                log.debug("last-version clientVersion:{} responseVersion:{}",clientVersion,version);
            }
            if (clientVersion.longValue()+1 < version.longValue()){
                lastVersion.put(key,clientVersion.longValue()+1);
            }else if (clientVersion.longValue()+1 == version.longValue()){
                lastVersion.put(key,version);
            }
        }else{
            lastVersion.put(key,version);
        }
    }

    /**
     * 写入 Environment 并且持久化到磁盘
     * @param properties
     * @param vo
     * @param mutablePropertySources
     */
    public static void buildEnvironmentAndWriteDisk(GlobalMarsProperties properties,ForDataVo vo,MutablePropertySources mutablePropertySources){
        String fileName = vo.getFileName();
        String fileType = vo.getFileType();
        String content = vo.getContent();



        ConfigTypeEnum configTypeEnum = ConfigTypeEnum.valueTypeOf(vo.getFileType());
        Properties value;
        if (configTypeEnum == ConfigTypeEnum.YAML || configTypeEnum == ConfigTypeEnum.PROPERTIES || configTypeEnum == ConfigTypeEnum.TEXT) {
            value = PropertiesSourceUtil.toProperties(content, configTypeEnum);
        }else{
            value = new Properties();
        }

        if (properties.isEnableLocalCache()){
            if (StringUtil.isEmpty(properties.getLocalCachePath())){
                properties.setLocalCachePath(FileUtil.getUserHome(properties.getAppName())) ;
            }
            //写入本地缓存文件
            ServerHttpAgent.writePathFile(properties.getLocalCachePath(),properties.getAppName(),properties.getEnvCode(),fileName,fileType,content);
        }

        String environmentFileName =  ApiConstant.NAME+fileName;
        MarsDataConfig dataConfig = new MarsDataConfig();
        dataConfig.setContent(content);

        MarsPropertySource marsPropertySource = new MarsPropertySource(environmentFileName, value, dataConfig);
        mutablePropertySources.addLast(marsPropertySource);
    }


    public static void buildEnv(ConfigurableEnvironment environment,
                                GlobalMarsProperties globalProperties,
                                String fileName,
                                String content,
                                String fileType){
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        if (mutablePropertySources==null){
            log.error("environment get property sources is null");
            return;
        }
        String environmentFileName =  ApiConstant.NAME+fileName;
        if (globalProperties!=null) {
            Properties properties = PropertiesSourceUtil.toProperties(content, ConfigTypeEnum.valueTypeOf(fileType));
            if (properties!=null){
                MarsDataConfig dataConfig = MarsDataConfig.builder()
                        .content(content)
                        .fileType(fileType)
                        .fileName(fileName)
                        .build();
                MarsPropertySource marsPropertySource = new MarsPropertySource(environmentFileName, properties, dataConfig);
                mutablePropertySources.addLast(marsPropertySource);
            }
        }
    }


    public static ForDataVoList getForData(Server server,String envCode,String appId,boolean first){

        List<String> params =getParams(envCode,appId,first);
        String url = getForDataRequestUrl(server);
        HttpResult httpResult =  HttpClientUtil.httpPost(url,null,params);
        if (httpResult.isSuccess()){
            return JsonUtil.parseObject(httpResult.getContent(), ForDataVoList.class);
        }
        return null;
    }

    /**
     * 请求最新 version，返回false 说明获取到了最新的version
     * @param server
     * @param env
     * @param appId
     * @param first
     * @return
     */
    public static boolean checkForUpdate(Server server, String env, String appId,boolean first) {

        List<String> params = getParams(env, appId,first);
        String url = getCheckForUpdateRequestUrl(server);

        HttpResult httpResult  = HttpClientUtil.httpPost(url,null,params,GlobalConstants.ENCODE_UTF8,2000,2000);
        if (httpResult.isSuccess() && StringUtil.isNotEmpty(httpResult.getContent()) ){
            long responseVersion = ObjectUtils.parseLong(httpResult.getContent());
            if (responseVersion == -1 || responseVersion == 0){
                return true;
            }
            String key = getKey(env,appId);
            if (lastVersion.containsKey(key)){
                Long last = lastVersion.get(key);
                if (last.longValue() < responseVersion) {
                    return false;
                }
            }

        }
        return true;
    }

    public static String getKey(String envCode,String appName){
        return envCode+appName;
    }

    private static String getCheckForUpdateRequestUrl(Server server) {
        String url ;
        if (server.getScheme() == SchemeEnum.HTTP) {
            url = String.format(ApiConstant.HTTP_CHECK_FOR_UPDATE_PATH_PARAM, server.getServerIp());
        } else {
            url = String.format(ApiConstant.HTTPS_CHECK_FOR_UPDATE_PATH_PARAM, server.getServerIp());
        }
        return url;
    }

    private static List<String> getParams(String env, String appId,boolean first) {
        List<String> params =new ArrayList<>(first?8:6);
        params.add("envCode");
        params.add(env);
        params.add("appId");
        params.add(appId);
        params.add("version");
        String key = getKey(env,appId);
        if (lastVersion.containsKey(key)) {
            params.add(lastVersion.get(key) + "");
        }else{
            params.add("0");
        }
        if (first){
            params.add("first");
            params.add("true");
        }
        return params;
    }

    private static String getForDataRequestUrl(Server server) {
        String url ;
        if (server.getScheme() == SchemeEnum.HTTP) {
            url = String.format(ApiConstant.HTTP_LOAD_DATA, server.getServerIp());
        } else {
            url = String.format(ApiConstant.HTTPS_LOAD_DATA, server.getServerIp());
        }
        return url;
    }


    /**
     * 写入本地指定位置缓存文件
     * @param localCachePath
     * @param appName
     * @param envCode
     * @param fileName
     * @param content
     */
    public static void writePathFile(String localCachePath,String appName,String envCode,String fileName,String fileType,String content){
        StringBuilder path = new StringBuilder();
        path.append(localCachePath).append(File.separator).append(ApiConstant.NAME);
        path.append(appName).append("_");
        path.append(envCode).append("_");
        path.append(fileName).append("_");
        path.append(fileType);
        if (log.isDebugEnabled()){
            log.debug("writePathFile path:{} content:{}",path,content);
        }
        File file =  new File(path.toString());
        if (file.exists()){
            FileUtil.deleteFile(file);
        }
        FileUtil.writeFile(new File(path.toString()),content);
    }


}
