package com.github.fashionbrot.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.req.ConfigRecordReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.dao.dao.ConfigRecordDao;
import com.github.fashionbrot.dao.entity.ConfigRecordEntity;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 配置数据记录表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-22
 */
@Service
public class ConfigRecordService {

    @Autowired
    private ConfigRecordDao configRecordDao;


    public Collection<ConfigRecordEntity> queryList(Map<String, Object> params) {
        return configRecordDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<ConfigRecordEntity>
     */
    public Collection<ConfigRecordEntity> configRecordByMap(Map<String, Object> params){
        return configRecordDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(ConfigRecordReq req){
        Page<?> page= PageHelper.startPage(req.getPage(),req.getPageSize());
        List<ConfigRecordEntity> list = configRecordDao.list(null);

        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }



    public void insert(ConfigRecordEntity entity) {
        if(!configRecordDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<ConfigRecordEntity> entityList) {
        return configRecordDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<ConfigRecordEntity> entityList, int batchSize) {
        return configRecordDao.saveBatch(entityList,batchSize);
    }


    public void updateById(ConfigRecordEntity entity) {
        if(!configRecordDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    public void update(ConfigRecordEntity entity, Wrapper<ConfigRecordEntity> updateWrapper) {
        if(!configRecordDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<ConfigRecordEntity> entityList) {
        return configRecordDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<ConfigRecordEntity> entityList, int batchSize) {
        return configRecordDao.updateBatchById(entityList,batchSize);
    }


    public ConfigRecordEntity selectById(Serializable id) {
        return configRecordDao.getById(id);
    }


    public void deleteById(Serializable id) {
        if(!configRecordDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            boolean result = configRecordDao.removeByIds(idList);
            if (!result){
                throw new CurdException(RespCode.DELETE_ERROR);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return configRecordDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<ConfigRecordEntity> queryWrapper) {
        return configRecordDao.remove(queryWrapper);
    }



}