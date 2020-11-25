package com.github.fashionbrot.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.fashionbrot.common.constant.MarsConst;
import com.github.fashionbrot.common.enums.DateEnum;
import com.github.fashionbrot.common.enums.OperationTypeEnum;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.req.ConfigValueApiReq;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.common.util.DateUtil;
import com.github.fashionbrot.common.util.SnowflakeIdWorkerUtil;
import com.github.fashionbrot.common.util.SystemUtil;
import com.github.fashionbrot.common.vo.ApiRespVo;
import com.github.fashionbrot.common.vo.ConfigValueVo;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.UserLoginService;
import com.github.fashionbrot.dao.dao.*;
import com.github.fashionbrot.dao.entity.*;
import com.github.fashionbrot.dao.mapper.TableColumnMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mysql.fabric.xmlrpc.base.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    @Autowired
    private TableColumnDao tableColumnDao;

    @Autowired
    private PropertyDao propertyDao;

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


        /*List<PropertyEntity> propertyList = propertyDao.getPropertyList(req.getAppName(),req.getTemplateKey());

        String tableName=MarsConst.TABLE_PREFIX+ req.getAppName()+"_"+req.getTemplateKey();
        req.setTableName(tableName);
        Page<?> page= PageHelper.startPage(req.getPage(),req.getPageSize());
        List<Map<String,Object>> list = configValueDao.configValueList(req);
        if (CollectionUtils.isNotEmpty(list)){
            for(Map<String,Object> map:list){
                configValueDao.formatDate(propertyList,map);
            }
        }*/

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
            ConfigReleaseEntity update= new ConfigReleaseEntity();
            update.setTemplateKeys(StringUtils.isEmpty(releaseEntity.getTemplateKeys())?templateKey:(releaseEntity.getTemplateKeys()+","+templateKey));
            if(!configReleaseDao.update(releaseEntity,q)){
                throw new CurdException(RespCode.FAIL);
            }
        }
    }

    public String getColumnType(List<TableColumnEntity> list,String key){
        if (CollectionUtils.isNotEmpty(list)){
            for(TableColumnEntity p:list){
                if (p.getColumnName().equals(key)){
                    return p.getDataType();
                }
            }
        }
        return "";
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

           /* List<PropertyEntity> list =  propertyDao.getPropertyList(configValue.getAppName(),configValue.getTemplateKey());
            //configValue.setTableName(configValue.getAppName()+"_"+configValue.getTemplateKey());
            Map<String,Object> map = tableColumnDao.selectTable(configValue);
            if (map!=null){
                for(Map.Entry<String,Object> mm: map.entrySet()) {
                    configValueDao.formatDate(list, mm,mm.getKey() );
                }
            }
            configValue.setValue(map);*/
        }
        return configValue;
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        ConfigValueEntity value = configValueDao.queryById(id);
        if (value==null){
            throw new MarsException("配置不存在");
        }
        value.setReleaseStatus(0);
        value.setDelFlag(0);
        if(configValueDao.updateDelete(id)!=1){
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

    private void loadValueCache(String envCode,String appName,boolean release){
        QueryWrapper<ConfigValueEntity> q=new QueryWrapper();
        q.eq("env_code",envCode);
        q.eq("app_name",appName);
        if (release) {
            q.in("release_status", Arrays.asList(0,2));
        }
        q.select("template_key");
        List<ConfigValueEntity> list = configValueDao.list(q);


        if (CollectionUtils.isNotEmpty(list)){
            List<String> releaseTemplateList = list.stream().distinct().map(m-> m.getTemplateKey()).collect(Collectors.toList());
            String key = getKey(envCode,appName);
            if (CollectionUtils.isNotEmpty(releaseTemplateList)){


                Map<String,List> tMap = new HashMap<>();
                for(String templateKey : releaseTemplateList){

                    q =new QueryWrapper();
                    q.eq("env_code",envCode);
                    q.eq("app_name",appName);

                    q.select("json,template_key");
                    q.orderByAsc("priority");
                    q.eq("template_key",templateKey);

                    List<ConfigValueEntity> valueList =  configValueDao.list(q);
                    if (CollectionUtils.isNotEmpty(valueList)){
                        List<JSONObject> json = valueList.stream().filter(m-> m!=null&&StringUtils.isNotEmpty(m.getJson()) ).map(m -> JSONObject.parseObject(m.getJson())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(json)){
                            tMap.put(templateKey,json);
                        }else{
                            tMap.put(templateKey,Collections.EMPTY_LIST);
                        }
                    }else{
                        tMap.put(templateKey,Collections.EMPTY_LIST);
                    }


                }
                cache.put(key,tMap );
            }
        }
    }

    private String getKey(String env,String app){
        return env+app;
    }


    public ApiRespVo getData(ConfigValueApiReq req) {

        QueryWrapper releaseQ = new QueryWrapper<ConfigReleaseEntity>().eq("env_code",req.getEnvCode()).eq("app_name",req.getAppId());
        ConfigReleaseEntity releaseEntity = configReleaseDao.getOne(releaseQ);
        if (releaseEntity==null){
            return ApiRespVo.builder().code(RespVo.SUCCESS).version(0L).build();
        }

        String key = getKey(req.getEnvCode(),req.getAppId());
        if(CollectionUtils.isEmpty(cache) || !cache.containsKey(key)){
            loadValueCache(req.getEnvCode(),req.getAppId(),false);
        }
        List<ConfigValueVo> list = null;
        if (cache.containsKey(key)) {
            //list = cache.get(key);
        }
        return ApiRespVo.builder().code(RespVo.SUCCESS).version(1L).data(list).build();
    }

    public Object checkVersion(ConfigValueApiReq req) {
        QueryWrapper releaseQ = new QueryWrapper<ConfigReleaseEntity>()
                .eq("env_code",req.getEnvCode())
                .eq("app_name",req.getAppId());
        ConfigReleaseEntity configReleaseEntity = configReleaseDao.getOne(releaseQ);
        if (configReleaseEntity==null){
            return 0;
        }
        return configReleaseEntity.getId();
    }
}