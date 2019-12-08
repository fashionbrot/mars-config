package com.gitee.mars.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.dao.entity.SystemConfigHistoryInfo;
import com.gitee.mars.dao.mapper.SystemConfigHistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
