package com.github.fashionbrot.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.dao.entity.AppInfo;
import com.github.fashionbrot.dao.mapper.AppInfoMapper;
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
public class AppInfoDao  {
    @Autowired
    private AppInfoMapper appInfoMapper;


    public Integer add(AppInfo appInfo) {

        if(appInfoMapper.selectCount(new QueryWrapper<AppInfo>().eq("app_name",appInfo.getAppName())) > 0){
            throw new MarsException(RespCode.EXIST_ERROR,"应用");
        }
        appInfo.setCreateDate(new Date());
        return appInfoMapper.insert(appInfo);
    }


    public Integer update(AppInfo appInfo) {

        appInfo.setUpdateDate(new Date());
        return appInfoMapper.update(appInfo,new QueryWrapper<AppInfo>().eq("app_name", appInfo.getAppName()));
    }


    public Integer deleteById(Long id) {
        return appInfoMapper.deleteById(id);
    }


    public AppInfo queryById(Long id) {
        return appInfoMapper.selectById(id);
    }


    public List<AppInfo> queryAll() {
        return appInfoMapper.selectList(null);
    }


    public AppInfo queryByAppName(String appName) {
        return appInfoMapper.selectOne(new QueryWrapper<AppInfo>().eq("app_name", appName));
    }


    public int deleteByAppName(String appName) {
        return appInfoMapper.delete(new QueryWrapper<AppInfo>().eq("app_name", appName));
    }

    public AppInfo selectOne(QueryWrapper<AppInfo> queryWrapper) {
        return appInfoMapper.selectOne(queryWrapper);
    }
}
