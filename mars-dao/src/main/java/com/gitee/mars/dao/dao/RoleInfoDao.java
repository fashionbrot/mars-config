package com.gitee.mars.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.dao.entity.RoleInfo;
import com.gitee.mars.dao.mapper.RoleInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RoleInfoDao  {

    @Autowired
    private RoleInfoMapper roleInfoMapper;

    public RoleInfo findByUserName( String userName){
        return roleInfoMapper.findByUserName(userName);
    }

    public RoleInfo findByUserId(Long userId){
        return roleInfoMapper.findByUserId(userId);
    }


    public List<RoleInfo> queryAll(QueryWrapper queryWrapper) {
        return roleInfoMapper.selectList(queryWrapper);
    }


    public int add(RoleInfo roleInfo) {
        roleInfo.setCreateDate(new Date());
        return roleInfoMapper.insert(roleInfo);
    }


    public int update(RoleInfo roleInfo) {
        roleInfo.setUpdateDate(new Date());
        return roleInfoMapper.updateById(roleInfo);
    }


    public int deleteById(Long id) {
        return roleInfoMapper.deleteById(id);
    }


    public RoleInfo queryById(Long id) {
        return roleInfoMapper.selectById(id);
    }
}
