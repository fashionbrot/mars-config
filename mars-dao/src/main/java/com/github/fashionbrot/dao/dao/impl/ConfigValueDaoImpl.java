package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.ConfigValueDao;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import com.github.fashionbrot.dao.mapper.ConfigValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigValueDaoImpl  extends ServiceImpl<ConfigValueMapper, ConfigValueEntity> implements ConfigValueDao {

    @Autowired
    private ConfigValueMapper configValueMapper;



}
