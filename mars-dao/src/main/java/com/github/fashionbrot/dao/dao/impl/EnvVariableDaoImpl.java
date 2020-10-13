package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.EnvVariableDao;
import com.github.fashionbrot.dao.entity.EnvVariableEntity;
import com.github.fashionbrot.dao.mapper.EnvVariableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnvVariableDaoImpl  extends ServiceImpl<EnvVariableMapper, EnvVariableEntity> implements EnvVariableDao {

    @Autowired
    private EnvVariableMapper envVariableMapper;



}
