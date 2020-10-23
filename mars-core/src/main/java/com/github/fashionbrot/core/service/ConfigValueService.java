package com.github.fashionbrot.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.fashionbrot.common.enums.DateEnum;
import com.github.fashionbrot.common.enums.OperationTypeEnum;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.common.util.DateUtil;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.core.UserLoginService;
import com.github.fashionbrot.dao.dao.ConfigRecordDao;
import com.github.fashionbrot.dao.dao.ConfigValueDao;
import com.github.fashionbrot.dao.dao.PropertyDao;
import com.github.fashionbrot.dao.entity.ConfigRecordEntity;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import com.github.fashionbrot.dao.entity.TableColumnEntity;
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
    private TableColumnMapper tableColumnMapper;

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


        List<PropertyEntity> propertyList = getPropertyList(req.getAppName(),req.getTemplateKey());

        String tableName=req.getAppName()+"_"+req.getTemplateKey();
        req.setTableName(tableName);
        Page<?> page= PageHelper.startPage(req.getPage(),req.getPageSize());
        List<Map<String,Object>> list = configValueDao.configValueList(req);
        if (CollectionUtils.isNotEmpty(list)){

            for(Map<String,Object> map:list){
                for(Map.Entry<String,Object> mm: map.entrySet()){
                    formatDate(propertyList,mm,mm.getKey());
                }
            }
        }

        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }

    private void formatDate(List<PropertyEntity> list, Map.Entry<String, Object> map, String column){
        if (CollectionUtils.isNotEmpty(list)){
            for(PropertyEntity p: list){
                DateEnum dateEnum = DateEnum.ofDateType(p.getPropertyType());
                if (dateEnum==DateEnum.YEAR && p.getPropertyKey().equals(column)){
                    java.sql.Date value= (java.sql.Date) map.getValue();
                    map.setValue(DateUtil.getYear(value));
                    break;
                }
                if (dateEnum==DateEnum.DATE && p.getPropertyKey().equals(column)){
                    java.sql.Date value= (java.sql.Date) map.getValue();
                    map.setValue(value.toString());
                    break;
                }
                if (dateEnum==DateEnum.TIME && p.getPropertyKey().equals(column)){
                    java.sql.Time value= (java.sql.Time) map.getValue();
                    map.setValue(value.toString());
                    break;
                }
                if (dateEnum!=null && p.getPropertyKey().equals(column)){
                    Date value = (Date) map.getValue();
                    if (value!=null){
                        map.setValue(DateUtil.formatDate(dateEnum.getPattern(),value));
                    }
                    break;
                }
            }
        }
    }

    private static Set<String> columnTypeString=new HashSet<>(Arrays.asList("datetime","date","time","year","varchar","text"));

    @Transactional(rollbackFor = Exception.class)
    public void insert(ConfigValueEntity entity) {
        if(!configValueDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }

        String tableName = entity.getAppName()+"_"+entity.getTemplateKey();
        List<TableColumnEntity> columnList = tableColumnMapper.selectList(new QueryWrapper<TableColumnEntity>().eq("table_name",tableName));
        StringBuilder sb =new StringBuilder();
        StringBuilder values =new StringBuilder();
        sb.append("insert into ").append(tableName).append(" ");

        JSONObject jsonObject = JSONObject.parseObject(entity.getJson());
        sb.append(" ( config_id");
        values.append("(").append(entity.getId());

        for (Map.Entry<String, Object> entry: jsonObject.entrySet()) {
            String columnType = getColumnType(columnList,entry.getKey());
            if (columnTypeString.contains(columnType)){
                sb.append(",").append(entry.getKey());
                values.append(",'").append(entry.getValue()).append("'");
            }else{
                sb.append(",").append(entry.getKey());
                values.append(",").append(entry.getValue());
            }
        }
        sb.append(" ) ");
        values.append(" ) ");
        sb.append(" values ");
        sb.append(values.toString());
        log.info("insert sql:" +sb.toString());
        tableColumnMapper.insertTable(sb.toString());


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



    private void setDate(ConfigValueEntity entity) {
        LoginModel model = userLoginService.getLogin();
        if (model!=null){

            entity.setUserName(new String(Base64.getDecoder().decode(model.getUserName())));
        }
        String json = entity.getJson();
        if (StringUtils.isEmpty(json)){
            throw new MarsException("请配置模板属性");
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(json);
        }catch (Exception e){
            throw new MarsException("填写属性值格式有误，请检查");
        }
        /*if (jsonObject!=null && jsonObject.containsKey("startDate") && StringUtils.isNotEmpty(jsonObject.getString("startDate"))){
            try {
                entity.setStartTime(DateUtils.parseDate(jsonObject.getString("startDate"),"yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject!=null && jsonObject.containsKey("endDate") && StringUtils.isNotEmpty(jsonObject.getString("endDate"))){
            try {
                entity.setEndTime(DateUtils.parseDate(jsonObject.getString("endDate"),"yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/
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
        ConfigValueEntity value = configValueDao.getById(entity.getId());
        if (value==null){
            throw new MarsException("配置不存在");
        }
        setDate(entity);
        entity.setReleaseStatus(0);
        if(!configValueDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }



        String tableName = entity.getAppName()+"_"+entity.getTemplateKey();
        List<TableColumnEntity> columnList = tableColumnMapper.selectList(new QueryWrapper<TableColumnEntity>().eq("table_name",tableName));

        StringBuilder sb =new StringBuilder();
        sb.append("update  ").append(tableName).append(" set ");
        JSONObject jsonObject = JSONObject.parseObject(entity.getJson());
        boolean isFlag=false;
        for (Map.Entry<String, Object> entry: jsonObject.entrySet()) {
            if (isFlag){
                sb.append(",");
            }
            String columnType = getColumnType(columnList,entry.getKey());
            if (columnTypeString.contains(columnType)){
                sb.append(entry.getKey()).append(" = ").append(" '").append(entry.getValue()).append("' ");
            }else{
                sb.append(entry.getKey()).append(" = ").append(" '").append(entry.getValue()).append("' ");
            }
            isFlag = true;
        }
        sb.append(" where  config_id = ").append(entity.getId());

        log.info("update sql:" +sb.toString());
        tableColumnMapper.updateTable(sb.toString());


        ConfigRecordEntity record=ConfigRecordEntity.builder()
                .appName(entity.getAppName())
                .envCode(entity.getEnvCode())
                .templateKey(entity.getTemplateKey())
                .json(JSON.toJSONString(value))
                .newJson(JSON.toJSONString(entity))
                .operationType(OperationTypeEnum.DELETE.getCode())
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
            configValue.setTableName(configValue.getAppName()+"_"+configValue.getTemplateKey());
            List<PropertyEntity> list =  getPropertyList(configValue.getAppName(),configValue.getTemplateKey());
            Map<String,Object> map = tableColumnMapper.selectTable(configValue);
            if (map!=null){
                for(Map.Entry<String,Object> mm: map.entrySet()) {
                    formatDate(list, mm,mm.getKey() );
                }
            }
            configValue.setValue(map);
        }
        return configValue;
    }

    public List<PropertyEntity> getPropertyList(String appName,String templateKey){
        QueryWrapper<PropertyEntity> queryWrapper=new QueryWrapper();
        queryWrapper.eq("app_name",appName);
        queryWrapper.eq("template_key",templateKey);
        return propertyDao.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Serializable id) {
        ConfigValueEntity value = configValueDao.getById(id);
        if (value==null){
            throw new MarsException("配置不存在");
        }
        if(!configValueDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }
        ConfigRecordEntity record=ConfigRecordEntity.builder()
                .appName(value.getAppName())
                .envCode(value.getEnvCode())
                .templateKey(value.getTemplateKey())
                .json(JSON.toJSONString(value))
                .newJson("")
                .operationType(OperationTypeEnum.DELETE.getCode())
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



}