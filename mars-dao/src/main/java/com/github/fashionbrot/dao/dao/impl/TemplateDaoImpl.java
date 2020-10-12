package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.TemplateDao;
import com.github.fashionbrot.dao.entity.TemplateEntity;
import com.github.fashionbrot.dao.mapper.TemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateDaoImpl  extends ServiceImpl<TemplateMapper, TemplateEntity> implements TemplateDao {

    @Autowired
    private TemplateMapper templateMapper;


    @Override
    public int deleteByTemplateKeyAndAppName(String templateKey, String appName) {
        return templateMapper.delete(new QueryWrapper<TemplateEntity>().eq("template_key",templateKey).eq("app_name",appName));
    }
}
