package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.ConfigReleaseDao;
import com.github.fashionbrot.dao.entity.ConfigReleaseEntity;
import com.github.fashionbrot.dao.mapper.ConfigReleaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigReleaseDaoImpl extends ServiceImpl<ConfigReleaseMapper, ConfigReleaseEntity> implements ConfigReleaseDao {

    @Autowired
    private ConfigReleaseMapper configReleaseMapper;


    @Override
    public Long getTopReleaseId(String envCode, String appName,Integer releaseFlag) {
        return configReleaseMapper.getTopReleaseId(envCode,appName,releaseFlag);
    }
}
