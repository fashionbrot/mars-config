package com.github.fashionbrot.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.enums.OperationTypeEnum;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.req.ConfigValueApiReq;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.common.util.SystemUtil;
import com.github.fashionbrot.common.vo.*;
import com.github.fashionbrot.core.UserLoginService;
import com.github.fashionbrot.dao.dao.*;
import com.github.fashionbrot.dao.entity.*;
import com.github.fashionbrot.ribbon.enums.SchemeEnum;
import com.github.fashionbrot.ribbon.loadbalancer.Server;
import com.github.fashionbrot.ribbon.util.HttpClientUtil;
import com.github.fashionbrot.ribbon.util.HttpResult;
import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 配置数据表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@Service
@Slf4j
public class ConfigValueService  {

    @Autowired
    private ConfigValueDao configValueDao;

    @Autowired
    private ConfigRecordDao configRecordDao;

    @Autowired
    private UserLoginService userLoginService;


    public Collection<ConfigValueEntity> queryList(Map<String, Object> params) {
        return configValueDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<ConfigValueEntity>
     */
    public Collection<ConfigValueEntity> configValueByMap(Map<String, Object> params){
        return configValueDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(ConfigValueReq req){

        Page<?> page= PageHelper.startPage(req.getPage(),req.getPageSize());
        List<Map<String,Object>> list = configValueDao.configValueList(req);
        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }




    @Transactional(rollbackFor = Exception.class)
    public void insert(ConfigValueEntity entity) {
        LoginModel login = userLoginService.getLogin();
        entity.setReleaseStatus(0);
        entity.setUserName(login.getUserName());

        if(!configValueDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }

        updateRelease(entity.getEnvCode(),entity.getAppName(),entity.getTemplateKey());
    }


    private void updateRelease(String envCode,String appName,String templateKey){
        QueryWrapper q = new QueryWrapper<ConfigReleaseEntity>()
                .eq("env_code",envCode)
                .eq("app_name",appName)
                .eq("release_flag",0);
        ConfigReleaseEntity releaseEntity = configReleaseDao.getOne(q);
        if (releaseEntity==null){
            releaseEntity = ConfigReleaseEntity.builder()
                    .envCode(envCode)
                    .appName(appName)
                    .templateKeys(templateKey)
                    .releaseFlag(0)
                    .build();
            if(!configReleaseDao.save(releaseEntity)){
                throw new CurdException(RespCode.FAIL);
            }
        }else{

            String oldKeys = releaseEntity.getTemplateKeys();
            if (StringUtils.isNotEmpty(oldKeys)){
                oldKeys=oldKeys+","+templateKey;
                List<String> keys = Arrays.stream(oldKeys.split(",")).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(keys)){
                    oldKeys = String.join(",",keys);
                }
            }else{
                oldKeys = templateKey;
            }

            ConfigReleaseEntity update= new ConfigReleaseEntity();
            update.setTemplateKeys(oldKeys);
            if(!configReleaseDao.update(update,q)){
                throw new CurdException(RespCode.FAIL);
            }
        }
    }



    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<ConfigValueEntity> entityList) {
        return configValueDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<ConfigValueEntity> entityList, int batchSize) {
        return configValueDao.saveBatch(entityList,batchSize);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateById(ConfigValueEntity entity) {
        ConfigValueEntity value = configValueDao.queryById(entity.getId());
        if (value==null){
            throw new MarsException("配置不存在");
        }
        LoginModel login = userLoginService.getLogin();

        entity.setReleaseStatus(0);
        entity.setUserName(login.getUserName());
        if(!configValueDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }

        updateRelease(entity.getEnvCode(),entity.getAppName(),entity.getTemplateKey());

        value.setJson(value.getJson());
        value.setValue(null);
        ConfigRecordEntity record=ConfigRecordEntity.builder()
                .appName(entity.getAppName())
                .envCode(entity.getEnvCode())
                .templateKey(entity.getTemplateKey())
                .configId(value.getId())
                .json(JSON.toJSONString(value))
                .newJson(JSON.toJSONString(entity))
                .operationType(OperationTypeEnum.UPDATE.getCode())
                .description(value.getDescription())
                .userName(login.getUserName())
                .build();
        configRecordDao.save(record);
    }


    public void update(ConfigValueEntity entity, Wrapper<ConfigValueEntity> updateWrapper) {
        if(!configValueDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<ConfigValueEntity> entityList) {
        return configValueDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<ConfigValueEntity> entityList, int batchSize) {
        return configValueDao.updateBatchById(entityList,batchSize);
    }


    public ConfigValueEntity selectById(Serializable id) {
        ConfigValueEntity configValue= configValueDao.getById(id);
        if (configValue!=null){
        }
        return configValue;
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        ConfigValueEntity value = configValueDao.queryById(id);
        if (value==null){
            throw new MarsException("配置不存在");
        }
        value.setReleaseStatus(2);
        if(!configValueDao.updateById(value)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }

        updateRelease(value.getEnvCode(),value.getAppName(),value.getTemplateKey());

        LoginModel login = userLoginService.getLogin();
        ConfigRecordEntity record=ConfigRecordEntity.builder()
                .appName(value.getAppName())
                .envCode(value.getEnvCode())
                .templateKey(value.getTemplateKey())
                .configId(value.getId())
                .json(value.getJson())
                .operationType(OperationTypeEnum.DELETE.getCode())
                .description(value.getDescription())
                .userName(login.getUserName())
                .build();
        configRecordDao.save(record);
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            boolean result = configValueDao.removeByIds(idList);
            if (!result){
                throw new CurdException(RespCode.DELETE_ERROR);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return configValueDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<ConfigValueEntity> queryWrapper) {
        return configValueDao.remove(queryWrapper);
    }

    private static Map<String,Map<String,List>> cache = new ConcurrentHashMap<>();


    @Autowired
    private ConfigReleaseDao configReleaseDao;

    @Autowired
    private Environment environment;

    private static final String CLUSTER="mars.value.cluster";

    @Transactional(rollbackFor = Exception.class)
    public void release(ConfigValueReq req) {
        QueryWrapper q = new QueryWrapper<ConfigReleaseEntity>()
                .eq("env_code",req.getEnvCode())
                .eq("app_name",req.getAppName())
                .eq("release_flag",0);
        ConfigReleaseEntity configReleaseEntity = configReleaseDao.getOne(q);
        if (configReleaseEntity==null){
            throw new MarsException("没有要发布的应用");
        }

        updateConfigValue(req.getEnvCode(),req.getAppName(),1,0);
        deleteByReleaseStatus(req.getEnvCode(),req.getAppName(),2);


        ConfigReleaseEntity updateRelease=ConfigReleaseEntity.builder()
                .releaseFlag(1)
                .build();
        if (!configReleaseDao.update(updateRelease,q)){
            throw new MarsException(RespCode.FAIL);
        }
        String key = getKey(req.getEnvCode(),req.getAppName());
        versionCache.put(key,configReleaseEntity.getId());

        if (environment.containsProperty(CLUSTER)){
            String cluster = environment.getProperty(CLUSTER);
            if (StringUtils.isEmpty(cluster)){
                return;
            }
            List<String> serverList = getServerList(cluster);
            int count = serverList.size();
            if (count<=0){
                return;
            }

            ExecutorService executorService = new ThreadPoolExecutor(count, count, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(count),
                    new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            if (!executor.isShutdown()) {
                                //再尝试入队
                                executor.execute(r);
                            }
                        }
                    });
            List<String> params = new ArrayList<>();
            params.add("envCode");
            params.add(req.getEnvCode());
            params.add("appId");
            params.add(req.getAppName());
            params.add("version");
            params.add(configReleaseEntity.getId()+"");

            for(String s : serverList){
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<3;i++){
                            HttpResult httpResult = HttpClientUtil.httpPost(s, null,params,"UTF-8",2000,2000);
                            if (httpResult!=null && httpResult.isSuccess() && (configReleaseEntity.getId().longValue()+"").equals(httpResult.getContent())){
                                break;
                            }
                        }
                    }
                });
            }
        }
    }

    private List<String> getServerList(String serverStr){

        String serverAddress = serverStr;
        String[] server = serverAddress.split(",");
        List<String> serverList=new ArrayList<>(server.length);
        if (StringUtil.isNotEmpty(serverAddress)) {
            for (String s : server) {
                String[] svr = s.split(":");
                int port = 80;
                if (svr.length == 2) {
                    port = StringUtil.parseInteger(svr[1], 80);
                }
                String ip = svr[0];
                if (!ip.startsWith("http")){
                    ip="http://"+ip;
                }
                ip = ip+":"+port+"/api/config/value/cluster/sync";
                serverList.add(ip);
            }
            return serverList;
        }
        return serverList;
    }

    private void updateConfigValue(String envCode,String appName,Integer updateReleaseStatus,Integer whereReleaseStatus){
        ConfigValueEntity update=new ConfigValueEntity();
        update.setReleaseStatus(updateReleaseStatus);
        QueryWrapper<ConfigValueEntity> qq=new QueryWrapper();
        qq.eq("env_code",envCode);
        qq.eq("app_name",appName);
        qq.eq("release_status",whereReleaseStatus);
        configValueDao.update(update,qq);
    }
    private void deleteByReleaseStatus(String envCode,String appName,Integer whereReleaseStatus){
        QueryWrapper<ConfigValueEntity> qq=new QueryWrapper();
        qq.eq("env_code",envCode);
        qq.eq("app_name",appName);
        qq.eq("release_status",whereReleaseStatus);
        configValueDao.remove(qq);
    }


    private String getKey(String env,String app){
        return env+app;
    }

    private List<JSONObject> format(List<JsonVo> jsonVos,String envCode,List<EnvVariableRelationEntity> variableRelationList){
        if (CollectionUtils.isNotEmpty(jsonVos)){
            return jsonVos.stream().map(m-> formatVariable(m.getJson(),envCode,variableRelationList) ).collect(Collectors.toList());
        }
        return null;
    }

    @Autowired
    private EnvVariableRelationDao envVariableRelationDao;

    private JSONObject formatVariable(String json,String envCode,List<EnvVariableRelationEntity> variableRelationList){

        if (StringUtils.isNotEmpty(json)){


            try {
                JSONObject map= JSONObject.parseObject(json);
                if (CollectionUtils.isNotEmpty(map)){
                    map.forEach((k,v)->{
                        String keyPrefix=k+"_prefix";
                        if (map.containsKey(keyPrefix)){
                            String valuePrefix= (String) map.get(keyPrefix);
                            /**
                             * 判断选择的是无前缀
                             */
                            if (StringUtils.isNotEmpty(valuePrefix) && !"-1".equals(valuePrefix)){
                                String  variableValue=getEnvVariableRelation(variableRelationList,envCode,valuePrefix);
                                if (StringUtils.isNotEmpty(variableValue)) {
                                    map.put(k, (variableValue + v.toString()));
                                }
                            }
                        }
                    });
                    return map;
                }
            }catch (Exception e){
                log.error("formatVariable error json:{}",json,e);
            }
        }
        return null;
    }

    private String getEnvVariableRelation(List<EnvVariableRelationEntity> envVariableRelationList,String envCode,String variableKey){
        if (CollectionUtils.isNotEmpty(envVariableRelationList)){
            for(EnvVariableRelationEntity var: envVariableRelationList){
                if (envCode.equals(var.getEnvCode()) && variableKey.equals(var.getVariableKey())){
                    return var.getVariableValue();
                }
            }
        }
        return "";
    }

    public ApiRespVo getData(ConfigValueApiReq req) {
        String key = getKey(req.getEnvCode(), req.getAppId());
        List<EnvVariableRelationEntity> variableRelationList = envVariableRelationDao.list(null);
        if ("1".equals(req.getAll())) {
            Map<String, Object> map = new HashMap<>();
            map.put("envCode", req.getEnvCode());
            map.put("appName", req.getAppId());
            List<ConfigValueVo> jsonList = configValueDao.selectByJson(map);
            if (CollectionUtils.isNotEmpty(jsonList)) {
                List<ConfigJsonVo> jj = jsonList.stream().map(m -> ConfigJsonVo.builder()
                        .templateKey(m.getTemplateKey())
                        .jsonList(format(m.getJsonList(), req.getEnvCode(),variableRelationList))
                        .build()).collect(Collectors.toList());

                return ApiRespVo.builder().code(RespVo.SUCCESS).version(versionCache.get(key)).data(jj).build();
            }

        } else {
            if (req.getVersion() != null && req.getVersion().longValue() == -1L) {
                return ApiRespVo.builder().code(RespVo.FAILED).msg("应用未配置信息").build();
            }
            ConfigReleaseEntity release = configReleaseDao.getById(req.getVersion());
            if (release == null) {
                return ApiRespVo.builder().code(RespVo.FAILED).msg("应用未配置信息").build();
            }
            if (StringUtils.isEmpty(release.getTemplateKeys())) {
                return ApiRespVo.builder().code(RespVo.SUCCESS).build();
            }

            if (!versionCache.containsKey(key)) {
                return ApiRespVo.builder().code(RespVo.FAILED).msg("应用未配置信息").build();
            }

            List<String> keyList = Arrays.stream(release.getTemplateKeys().split(",")).collect(Collectors.toList());
            Map<String, Object> map = new HashMap<>();
            map.put("envCode", release.getEnvCode());
            map.put("appName", release.getAppName());

            map.put("templateKeyList", keyList);
            List<ConfigValueVo> jsonList = configValueDao.selectByJson(map);
            if (CollectionUtils.isNotEmpty(jsonList)) {
                List<ConfigJsonVo> jj = jsonList.stream().map(m -> ConfigJsonVo.builder()
                        .templateKey(m.getTemplateKey())
                        .jsonList(format(m.getJsonList(), req.getEnvCode(),variableRelationList))
                        .build()).collect(Collectors.toList());
                return ApiRespVo.builder().code(RespVo.SUCCESS).version(versionCache.get(key)).data(jj).build();
            }
        }
        return ApiRespVo.builder().code(RespVo.SUCCESS).version(versionCache.get(key)).data(Collections.EMPTY_LIST).build();
    }



    private Map<String,Long> versionCache = new ConcurrentHashMap<>();

    public Object checkVersion(ConfigValueApiReq req) {
        String key =  getKey(req.getEnvCode(),req.getAppId());
        if (versionCache.containsKey(key)){
            return versionCache.get(key);
        }

        Long version = configReleaseDao.getTopReleaseId(req.getEnvCode(),req.getAppId(),1);
        if (version==null){
            versionCache.put(key,-1L);
            return -1;
        }else{
            versionCache.put(key,version);
            return version;
        }
    }


    public Long clusterSync(ConfigValueApiReq req) {
        String key = getKey(req.getEnvCode(),req.getAppId());
        long version =req.getVersion().longValue();
        long v = 0;
        if (versionCache.containsKey(key)){
            v = versionCache.get(key).longValue();
        }
        if(v<version){
            versionCache.put(key,req.getVersion());
        }
        return versionCache.get(key);
    }
}