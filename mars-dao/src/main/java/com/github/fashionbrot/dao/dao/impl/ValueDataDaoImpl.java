package com.github.fashionbrot.dao.dao.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fashionbrot.dao.dao.ValueDataDao;
import com.github.fashionbrot.dao.entity.ValueDataEntity;
import com.github.fashionbrot.dao.mapper.ValueDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValueDataDaoImpl  extends ServiceImpl<ValueDataMapper, ValueDataEntity> implements ValueDataDao {

    @Autowired
    private ValueDataMapper valueDataMapper;



}
