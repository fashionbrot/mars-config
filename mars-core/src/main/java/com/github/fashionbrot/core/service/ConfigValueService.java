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

        if(!configValueDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }

        /*String sql = tableColumnDao.getInsertSql(entity);
        if (log.isDebugEnabled()) {
            log.info("insert sql:" + sql);
        }
        tableColumnDao.insertTable(sql);*/
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

       /* String sql  = tableColumnDao.getUpdateConfigSql(entity);
        log.info("update sql:" +sql);
        tableColumnDao.updateTable(sql);*/


        /*List<PropertyEntity> propertyList = propertyDao.getPropertyList(entity.getAppName(),entity.getTemplateKey());
        configValueDao.formatDate(propertyList,value.getValue());*/

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
        if(!configValueDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }

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

    private static Map<String,List<ConfigValueVo>> cache = new ConcurrentHashMap<>();

    @Autowired
    private ConfigReleaseDao configReleaseDao;

    @Transactional(rollbackFor = Exception.class)
    public void release(ConfigValueReq req) {
        QueryWrapper releaseQ = new QueryWrapper<ConfigReleaseEntity>().eq("env_code",req.getEnvCode()).eq("app_name",req.getAppName());
        ConfigReleaseEntity configReleaseEntity = configReleaseDao.getOne(releaseQ);
        if (configReleaseEntity==null){
            configReleaseEntity = ConfigReleaseEntity.builder()
                    .envCode(req.getEnvCode())
                    .appName(req.getAppName())
                    .version(1L)
                    .build();
        }


        loadValueCache(req.getEnvCode(),req.getAppName(),0);

        ConfigValueEntity update=new ConfigValueEntity();
        update.setReleaseStatus(1);
        QueryWrapper<ConfigValueEntity> qq=new QueryWrapper();
        qq.eq("env_code",req.getEnvCode());
        qq.eq("app_name",req.getAppName());
        qq.eq("release_status",0);
        configValueDao.update(update,qq);

        if (configReleaseEntity.getId()==null){
            configReleaseDao.save(configReleaseEntity);
        }else{
            ConfigReleaseEntity updateRelease=ConfigReleaseEntity.builder()
                    .version((configReleaseEntity.getVersion()+1))
                    .build();
            releaseQ.eq("version",configReleaseEntity.getVersion());
            configReleaseDao.update(updateRelease,releaseQ);
        }
    }

    private void loadValueCache(String envCode,String appName,Integer releaseStatus){
        QueryWrapper<ConfigValueEntity> q=new QueryWrapper();
        q.eq("env_code",envCode);
        q.eq("app_name",appName);
        if (releaseStatus==0) {
            q.eq("release_status", 0);
        }
        q.select("template_key");
        List<ConfigValueEntity> list = configValueDao.list(q);
        if (CollectionUtils.isNotEmpty(list)){

            List<String> releaseTemplateList = list.stream().distinct().map(m-> m.getTemplateKey()).collect(Collectors.toList());
            String key = getKey(envCode,appName);
            if (CollectionUtils.isNotEmpty(releaseTemplateList)){
                q =new QueryWrapper();
                q.eq("env_code",envCode);
                q.eq("app_name",appName);
                if (releaseStatus==0) {
                    q.eq("release_status", 0);
                }
                q.select("json");
                q.orderByAsc("priority");
                List<ConfigValueVo> allList= new ArrayList<>();
                for(String templateKey : releaseTemplateList){
                    q.eq("template_key",templateKey);
                    List<ConfigValueEntity> valueList =  configValueDao.list(q);
                    if (CollectionUtils.isNotEmpty(valueList)){
                        List<JSONObject> json = valueList.stream().map(m -> JSONObject.parseObject(m.getJson())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(json)){
                            allList.add(ConfigValueVo.builder()
                                    .templateKey(templateKey)
                                    .jsonList(json).build());
                        }
                    }
                }
                cache.put(key,allList );
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
            loadValueCache(req.getEnvCode(),req.getAppId(),1);
        }
        List<ConfigValueVo> list = null;
        if (cache.containsKey(key)) {
            list = cache.get(key);
        }
        return ApiRespVo.builder().code(RespVo.SUCCESS).version(releaseEntity.getVersion()).data(list).build();
    }

    public Object checkVersion(ConfigValueApiReq req) {
        QueryWrapper releaseQ = new QueryWrapper<ConfigReleaseEntity>()
                .eq("env_code",req.getEnvCode())
                .eq("app_name",req.getAppId());
        ConfigReleaseEntity configReleaseEntity = configReleaseDao.getOne(releaseQ);
        if (configReleaseEntity==null){
            return 0;
        }
        return configReleaseEntity.getVersion();
    }
}