package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.TemplatePropertyRelationDao;
import com.github.fashionbrot.dao.entity.TemplatePropertyRelationEntity;
import com.github.fashionbrot.dao.mapper.TemplatePropertyRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplatePropertyRelationDaoImpl  extends ServiceImpl<TemplatePropertyRelationMapper, TemplatePropertyRelationEntity> implements TemplatePropertyRelationDao {

    @Autowired
    private TemplatePropertyRelationMapper templatePropertyRelationMapper;


    @Override
    public int coountTemplateKeyAndAppName(String templateKey, String appName) {
        return templatePropertyRelationMapper.selectCount(new QueryWrapper<TemplatePropertyRelationEntity>()
                .eq("template_key",templateKey)
                .eq("app_name",appName)
        );
    }
}
