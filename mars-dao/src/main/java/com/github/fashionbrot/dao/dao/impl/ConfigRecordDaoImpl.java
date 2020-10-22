package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.ConfigRecordDao;
import com.github.fashionbrot.dao.entity.ConfigRecordEntity;
import com.github.fashionbrot.dao.mapper.ConfigRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigRecordDaoImpl extends ServiceImpl<ConfigRecordMapper, ConfigRecordEntity> implements ConfigRecordDao {

    @Autowired
    private ConfigRecordMapper configRecordMapper;



}
