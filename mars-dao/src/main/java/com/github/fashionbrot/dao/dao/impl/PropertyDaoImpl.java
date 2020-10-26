package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.PropertyDao;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import com.github.fashionbrot.dao.mapper.PropertyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyDaoImpl  extends ServiceImpl<PropertyMapper, PropertyEntity> implements PropertyDao {

    @Autowired
    private PropertyMapper propertyMapper;


    @Override
    public List<PropertyEntity> getPropertyList(String appName, String templateKey){
        QueryWrapper<PropertyEntity> queryWrapper=new QueryWrapper();
        queryWrapper.eq("app_name",appName);
        queryWrapper.eq("template_key",templateKey);
        return propertyMapper.selectList(queryWrapper);
    }

}
