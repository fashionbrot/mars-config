package com.github.fashionbrot.dao.dao.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.common.constant.MarsConst;
import com.github.fashionbrot.common.enums.DateEnum;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.common.util.DateUtil;
import com.github.fashionbrot.dao.dao.ConfigValueDao;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import com.github.fashionbrot.dao.entity.TableColumnEntity;
import com.github.fashionbrot.dao.mapper.ConfigValueMapper;
import com.github.fashionbrot.dao.mapper.TableColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ConfigValueDaoImpl  extends ServiceImpl<ConfigValueMapper, ConfigValueEntity> implements ConfigValueDao {

    @Autowired
    private ConfigValueMapper configValueMapper;

    @Autowired
    private TableColumnMapper tableColumnMapper;



    @Override
    public    List<Map<String,Object>> configValueList(ConfigValueReq req) {
        return configValueMapper.configValueList(req);
    }

    @Override
    public ConfigValueEntity queryById(Long id) {
        ConfigValueEntity entity = configValueMapper.selectById(id);
        if (entity!=null){
            entity.setTableName(entity.getAppName()+"_"+entity.getTemplateKey());
            Map<String, Object> map = tableColumnMapper.selectTable(entity);
            entity.setValue(map);
        }
        return entity;
    }

    @Override
    public String getUpdateConfigSql(ConfigValueEntity entity) {

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
            if (MarsConst.columnTypeString.contains(columnType)){
                sb.append(entry.getKey()).append(" = ").append(" '").append(entry.getValue()).append("' ");
            }else{
                sb.append(entry.getKey()).append(" = ").append(" '").append(entry.getValue()).append("' ");
            }
            isFlag = true;
        }
        sb.append(" where  config_id = ").append(entity.getId());

        return sb.toString();
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

    public void formatDate(List<PropertyEntity> list, Map<String, Object> map){
        if (CollectionUtils.isNotEmpty(map)){
            for(Map.Entry<String,Object> mm: map.entrySet()){
                formatDate(list,mm,mm.getKey());
            }
        }
    }

    public void formatDate(List<PropertyEntity> list, Map.Entry<String, Object> map, String column){
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


}
