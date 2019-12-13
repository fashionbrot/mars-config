package com.github.fashionbrot.dao.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.dao.entity.EnvInfo;
import com.github.fashionbrot.dao.mapper.EnvInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Service
public class EnvInfoDao {
    @Autowired
    private EnvInfoMapper envInfoMapper;


    public Integer add(EnvInfo envInfo) {
        if(envInfoMapper.selectCount(new QueryWrapper<EnvInfo>().eq("env_name",envInfo.getEnvName())) != 0){
           throw new MarsException(RespCode.EXIST_ERROR,"环境名称");
        }

        if(envInfoMapper.selectCount(new QueryWrapper<EnvInfo>().eq("env_code",envInfo.getEnvCode())) != 0){
            throw new MarsException(RespCode.EXIST_ERROR,"环境Code");
        }
        envInfo.setCreateDate(new Date());
        return envInfoMapper.insert(envInfo);
    }


    public Integer update(EnvInfo envInfo) {
        EnvInfo envInfoName = envInfoMapper.selectOne(new QueryWrapper<EnvInfo>().eq("env_name", envInfo.getEnvName()));
        if(envInfoName != null && envInfoName.getId().equals(envInfo.getId())){
            throw new MarsException(RespCode.EXIST_ERROR,"环境名称");
        }

        envInfo.setUpdateDate(new Date());
        return envInfoMapper.updateById(envInfo);
    }


    public Integer deleteById(Long id) {
        return envInfoMapper.deleteById(id);
    }


    public EnvInfo queryById(Long id) {
        return envInfoMapper.selectById(id);
    }


    public List<EnvInfo> queryAll(QueryWrapper queryWrapper) {
        return envInfoMapper.selectList(queryWrapper);
    }
}
