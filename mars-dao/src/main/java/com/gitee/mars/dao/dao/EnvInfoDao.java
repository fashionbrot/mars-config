package com.gitee.mars.dao.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.dao.entity.EnvInfo;
import com.gitee.mars.dao.mapper.EnvInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EnvInfoDao {
    @Autowired
    private EnvInfoMapper envInfoDao;


    public Integer add(EnvInfo envInfo) {
        if(envInfoDao.selectCount(new QueryWrapper<EnvInfo>().eq("env_name",envInfo.getEnvName())) != 0){
           throw new MarsException(RespCode.EXIST_ERROR,"环境名称");
        }

        if(envInfoDao.selectCount(new QueryWrapper<EnvInfo>().eq("env_code",envInfo.getEnvCode())) != 0){
            throw new MarsException(RespCode.EXIST_ERROR,"环境Code");
        }
        envInfo.setCreateDate(new Date());
        return envInfoDao.insert(envInfo);
    }


    public Integer update(EnvInfo envInfo) {
        EnvInfo envInfoName = envInfoDao.selectOne(new QueryWrapper<EnvInfo>().eq("env_name", envInfo.getEnvName()));
        if(envInfoName != null && envInfoName.getId().equals(envInfo.getId())){
            throw new MarsException(RespCode.EXIST_ERROR,"环境名称");
        }

        envInfo.setUpdateDate(new Date());
        return envInfoDao.updateById(envInfo);
    }


    public Integer deleteById(Long id) {
        return envInfoDao.deleteById(id);
    }


    public EnvInfo queryById(Long id) {
        return envInfoDao.selectById(id);
    }


    public List<EnvInfo> queryAll(QueryWrapper queryWrapper) {
        return envInfoDao.selectList(queryWrapper);
    }
}
