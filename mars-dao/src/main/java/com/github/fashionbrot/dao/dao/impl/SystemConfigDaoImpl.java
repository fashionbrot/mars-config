package com.github.fashionbrot.dao.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.SystemConfigDao;
import com.github.fashionbrot.dao.dto.SystemConfigDto;
import com.github.fashionbrot.dao.entity.SystemConfigInfo;
import com.github.fashionbrot.dao.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigDaoImpl extends ServiceImpl<SystemConfigMapper, SystemConfigInfo> implements SystemConfigDao {

    @Autowired
    private SystemConfigMapper systemConfigMapper;


    @Override
    public int updateRelease(SystemConfigDto build) {
        return systemConfigMapper.updateRelease(build);
    }
}
