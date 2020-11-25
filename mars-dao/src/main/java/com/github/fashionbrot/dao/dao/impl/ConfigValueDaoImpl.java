package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.common.enums.DateEnum;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.common.util.DateUtil;
import com.github.fashionbrot.common.vo.ConfigValueVo;
import com.github.fashionbrot.dao.dao.ConfigValueDao;
import com.github.fashionbrot.dao.dao.TableColumnDao;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import com.github.fashionbrot.dao.mapper.ConfigValueMapper;
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
    private TableColumnDao tableColumnDao;


    @Override
    public int updateDelete(Long id) {
        return configValueMapper.updateDelete(id);
    }

    @Override
    public    List<Map<String,Object>> configValueList(ConfigValueReq req) {
        return configValueMapper.configValueList(req);
    }

    @Override
    public ConfigValueEntity queryById(Long id) {
        ConfigValueEntity entity = configValueMapper.selectById(id);
        if (entity!=null){
            /*entity.setTableName(entity.getAppName()+"_"+entity.getTemplateKey());
            Map<String, Object> map = tableColumnDao.selectTable(entity);
            entity.setValue(map);*/
        }
        return entity;
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

    @Override
    public List<ConfigValueVo> selectByJson(Map<String,Object> map) {
        return configValueMapper.selectByJson( map);
    }


}
