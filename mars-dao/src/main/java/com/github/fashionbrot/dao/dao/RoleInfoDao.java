package com.github.fashionbrot.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.dao.entity.RoleInfo;
import com.github.fashionbrot.dao.mapper.RoleInfoMapper;
import lombok.extern.slf4j.Slf4j;
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
