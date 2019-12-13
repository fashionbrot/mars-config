package com.github.fashionbrot.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.dao.entity.SystemConfigHistoryInfo;
import com.github.fashionbrot.dao.mapper.SystemConfigHistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Service
@Slf4j
public class SystemConfigHistoryDao {

    @Autowired
    private SystemConfigHistoryMapper systemConfigHistoryMapper;


    public void insert(SystemConfigHistoryInfo generateHistoryInfo) {
        if (systemConfigHistoryMapper.insert(generateHistoryInfo)!=1){
            throw new MarsException(RespCode.SAVE_ERROR,"历史记录");
        }
    }

    public List<SystemConfigHistoryInfo> selectList(QueryWrapper<SystemConfigHistoryInfo> eq) {
        return systemConfigHistoryMapper.selectList(eq);
    }

    public SystemConfigHistoryInfo selectById(Long id) {
        return systemConfigHistoryMapper.selectById(id);
    }

    public int deleteById(Long id) {
        return systemConfigHistoryMapper.deleteById(id);
    }
}
