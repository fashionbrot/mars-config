package com.github.fashionbrot.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.req.EnvVariableRelationReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.dao.dao.EnvVariableRelationDao;
import com.github.fashionbrot.dao.entity.EnvVariableRelationEntity;
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
 * 常量和环境关系表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@Service
public class EnvVariableRelationService {

    @Autowired
    private EnvVariableRelationDao envVariableRelationDao;


    public Collection<EnvVariableRelationEntity> queryList(Map<String, Object> params) {
        return envVariableRelationDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<EnvVariableRelationEntity>
     */
    public Collection<EnvVariableRelationEntity> envVariableRelationByMap(Map<String, Object> params){
        return envVariableRelationDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(EnvVariableRelationReq req){
        Page<?> page= PageHelper.startPage((req.getStart() / req.getLength()) + 1, req.getLength());
        List<EnvVariableRelationEntity> list = envVariableRelationDao.list(null);

        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }



    public void insert(EnvVariableRelationEntity entity) {
        if(!envVariableRelationDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<EnvVariableRelationEntity> entityList) {
        return envVariableRelationDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<EnvVariableRelationEntity> entityList, int batchSize) {
        return envVariableRelationDao.saveBatch(entityList,batchSize);
    }


    public void updateById(EnvVariableRelationEntity entity) {
        if(!envVariableRelationDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    public void update(EnvVariableRelationEntity entity, Wrapper<EnvVariableRelationEntity> updateWrapper) {
        if(!envVariableRelationDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<EnvVariableRelationEntity> entityList) {
        return envVariableRelationDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<EnvVariableRelationEntity> entityList, int batchSize) {
        return envVariableRelationDao.updateBatchById(entityList,batchSize);
    }


    public EnvVariableRelationEntity selectById(Serializable id) {
        return envVariableRelationDao.getById(id);
    }


    public void deleteById(Serializable id) {
        if(!envVariableRelationDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            boolean result = envVariableRelationDao.removeByIds(idList);
            if (!result){
                throw new CurdException(RespCode.DELETE_ERROR);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return envVariableRelationDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<EnvVariableRelationEntity> queryWrapper) {
        return envVariableRelationDao.remove(queryWrapper);
    }



}