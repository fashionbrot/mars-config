package com.github.fashionbrot.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.req.ValueDataReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.dao.dao.ValueDataDao;
import com.github.fashionbrot.dao.entity.ValueDataEntity;
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
 * 导入导出记录表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@Service
public class ValueDataService {

    @Autowired
    private ValueDataDao valueDataDao;


    public Collection<ValueDataEntity> queryList(Map<String, Object> params) {
        return valueDataDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<ValueDataEntity>
     */
    public Collection<ValueDataEntity> valueDataByMap(Map<String, Object> params){
        return valueDataDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(ValueDataReq req){
        Page<?> page= PageHelper.startPage((req.getStart() / req.getLength()) + 1, req.getLength());
        List<ValueDataEntity> list = valueDataDao.list(null);

        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }



    public void insert(ValueDataEntity entity) {
        if(!valueDataDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<ValueDataEntity> entityList) {
        return valueDataDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<ValueDataEntity> entityList, int batchSize) {
        return valueDataDao.saveBatch(entityList,batchSize);
    }


    public void updateById(ValueDataEntity entity) {
        if(!valueDataDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    public void update(ValueDataEntity entity, Wrapper<ValueDataEntity> updateWrapper) {
        if(!valueDataDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<ValueDataEntity> entityList) {
        return valueDataDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<ValueDataEntity> entityList, int batchSize) {
        return valueDataDao.updateBatchById(entityList,batchSize);
    }


    public ValueDataEntity selectById(Serializable id) {
        return valueDataDao.getById(id);
    }


    public void deleteById(Serializable id) {
        if(!valueDataDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            boolean result = valueDataDao.removeByIds(idList);
            if (!result){
                throw new CurdException(RespCode.DELETE_ERROR);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return valueDataDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<ValueDataEntity> queryWrapper) {
        return valueDataDao.remove(queryWrapper);
    }



}