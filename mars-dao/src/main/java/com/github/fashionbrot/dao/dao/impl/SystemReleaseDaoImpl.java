package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.SystemReleaseDao;
import com.github.fashionbrot.dao.entity.SystemReleaseEntity;
import com.github.fashionbrot.dao.mapper.SystemReleaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemReleaseDaoImpl extends ServiceImpl<SystemReleaseMapper, SystemReleaseEntity> implements SystemReleaseDao {

    @Autowired
    private SystemReleaseMapper systemReleaseMapper;


    @Override
    public Long getTopReleaseId(String envCode, String appName,Integer releaseFlag) {
        return systemReleaseMapper.getTopReleaseId(envCode,appName,releaseFlag);
    }
}
