package com.github.fashionbrot.dao.dao;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.constant.MarsConst;
import com.github.fashionbrot.common.enums.OptionEnum;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import com.github.fashionbrot.dao.entity.TableColumnEntity;
import com.github.fashionbrot.dao.mapper.TableColumnMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class TableColumnDao {


    @Autowired
    private TableColumnMapper tableColumnMapper;

    public static Set<String> columnTypeString=new HashSet<>(Arrays.asList("datetime","date","time","year","text"));

    public static Set<String> columnType=new HashSet<>(Arrays.asList("varchar","int","bigint","tinyint"));


    public void addTable(@Param("tableName")String tableName){
        if (!tableName.startsWith( MarsConst.TABLE_PREFIX )){
            tableName =  MarsConst.TABLE_PREFIX + tableName;
        }
        tableColumnMapper.addTable(tableName);
    }


    public void dropTable(String tableName) {
        if (!tableName.startsWith( MarsConst.TABLE_PREFIX )){
            tableName =  MarsConst.TABLE_PREFIX + tableName;
        }
        tableColumnMapper.dropTable(tableName);
    }


    public void operateTableColumn(PropertyEntity entity,OptionEnum optionEnum){
        String tableName = entity.getAppName()+"_"+entity.getTemplateKey();
        List<PropertyEntity> list =new ArrayList<>(2);
        list.add(entity);

        if(StringUtils.isNotEmpty(entity.getVariableKey())){
            list.add(PropertyEntity.builder()
                    .templateKey(entity.getTemplateKey())
                    .propertyKey(entity.getPropertyKey()+ MarsConst.PROPERTY_PREFIX)
                    .propertyType("varchar")
                    .columnLength(200)
                    .build());
        }
        editTableColumn(tableName,list,optionEnum);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editTableColumn(String tableName,List<PropertyEntity> list, OptionEnum option) {
        if (CollectionUtils.isNotEmpty(list)){
            if (!tableName.startsWith( MarsConst.TABLE_PREFIX )){
                tableName =  MarsConst.TABLE_PREFIX + tableName;
            }
            StringBuilder sb=new StringBuilder();
            sb.append(" alter table ").append(tableName).append(" ");
            boolean first = true;
            for (PropertyEntity p : list){


                String propertyType = p.getPropertyType();
                String propertyKey = p.getPropertyKey();

                if (!first){
                    sb.append(",");
                }
                if (StringUtils.isEmpty(p.getOption())) {
                    sb.append(option.getOption()).append(" ");
                }else{
                    sb.append(p.getOption()).append(" ");
                }
                sb.append(propertyKey).append(" ");
                if (option != OptionEnum.DROP){
                    if (columnTypeString.contains(propertyType)){
                        sb.append(propertyType).append(" default null ");
                    }else if ("decimal".equals(propertyType) || "double".equals(propertyType)){
                        sb.append(propertyType).append("(11,2) ");
                    }else if (columnType.contains(propertyType)){
                        sb.append(propertyType).append("(").append(p.getColumnLength()).append(") ");
                    }
                }

                first = false;
            }
            sb.append(";");
            String sql = sb.toString();
            if (log.isDebugEnabled()){
                log.debug("editTableColumn sql:"+sql);
            }
            tableColumnMapper.updateTable(sql);
        }
    }

    public List<TableColumnEntity> selectList(String tableName) {
        if (!tableName.startsWith( MarsConst.TABLE_PREFIX )){
            tableName =  MarsConst.TABLE_PREFIX + tableName;
        }
        return tableColumnMapper.selectList(new QueryWrapper<TableColumnEntity>().eq("table_name",tableName));
    }


    public String getUpdateConfigSql(ConfigValueEntity entity) {

        String tableName =MarsConst.TABLE_PREFIX+ entity.getAppName()+"_"+entity.getTemplateKey();
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
            if (columnTypeString.contains(columnType) || "varchar".equals(columnType)){
                sb.append(entry.getKey()).append(" = ").append(" '").append(entry.getValue()).append("' ");
            }else{
                sb.append(entry.getKey()).append(" = ").append(" ").append(entry.getValue()).append(" ");
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

    public void updateTable(String sql) {
        tableColumnMapper.updateTable(sql);
    }

    public Map<String, Object> selectTable(ConfigValueEntity configValue) {
        if (!configValue.getTableName().startsWith( MarsConst.TABLE_PREFIX )){
            configValue.setTableName(MarsConst.TABLE_PREFIX + configValue.getTableName());
        }
        return tableColumnMapper.selectTable(configValue);
    }

    public String getInsertSql(ConfigValueEntity entity) {

        String tableName = MarsConst.TABLE_PREFIX + entity.getAppName()+"_"+entity.getTemplateKey();

        List<TableColumnEntity> columnList = selectList(tableName);
        StringBuilder sb =new StringBuilder();
        StringBuilder values =new StringBuilder();
        sb.append("insert into ").append(tableName).append(" ");

        JSONObject jsonObject = JSONObject.parseObject(entity.getJson());
        sb.append(" ( config_id");
        values.append("(").append(entity.getId());

        for (Map.Entry<String, Object> entry: jsonObject.entrySet()) {
            String columnType = getColumnType(columnList,entry.getKey());
            if (columnTypeString.contains(columnType) || "varchar".equals(columnType)){
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

        return sb.toString();
    }

    public void insertTable(String sql) {
        tableColumnMapper.insertTable(sql);
    }
}
