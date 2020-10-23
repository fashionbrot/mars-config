package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.dao.dao.ConfigValueDao;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import com.github.fashionbrot.dao.mapper.ConfigValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConfigValueDaoImpl  extends ServiceImpl<ConfigValueMapper, ConfigValueEntity> implements ConfigValueDao {

    @Autowired
    private ConfigValueMapper configValueMapper;


    @Override
    public    List<Map<String,Object>> configValueList(ConfigValueReq req) {
        return configValueMapper.configValueList(req);
    }
}
