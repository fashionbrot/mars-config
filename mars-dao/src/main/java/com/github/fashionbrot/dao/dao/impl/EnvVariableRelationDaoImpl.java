package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.EnvVariableRelationDao;
import com.github.fashionbrot.dao.entity.EnvVariableRelationEntity;
import com.github.fashionbrot.dao.mapper.EnvVariableRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnvVariableRelationDaoImpl  extends ServiceImpl<EnvVariableRelationMapper, EnvVariableRelationEntity> implements EnvVariableRelationDao {

    @Autowired
    private EnvVariableRelationMapper envVariableRelationMapper;



}
