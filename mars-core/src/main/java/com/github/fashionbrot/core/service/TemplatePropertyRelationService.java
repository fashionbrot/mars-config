package com.github.fashionbrot.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.req.TemplatePropertyRelationReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.dao.dao.TemplatePropertyRelationDao;
import com.github.fashionbrot.dao.entity.TemplatePropertyRelationEntity;
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
 * 模板属性关系表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */
@Service
public class TemplatePropertyRelationService {

    @Autowired
    private TemplatePropertyRelationDao templatePropertyRelationDao;


    public Collection<TemplatePropertyRelationEntity> queryList(Map<String, Object> params) {
        return templatePropertyRelationDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<TemplatePropertyRelationEntity>
     */
    public Collection<TemplatePropertyRelationEntity> templatePropertyRelationByMap(Map<String, Object> params){
        return templatePropertyRelationDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(TemplatePropertyRelationReq req){
        Page<?> page= PageHelper.startPage((req.getStart() / req.getLength()) + 1, req.getLength());
        List<TemplatePropertyRelationEntity> list = templatePropertyRelationDao.list(null);

        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }



    public void insert(TemplatePropertyRelationEntity entity) {
        if(!templatePropertyRelationDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<TemplatePropertyRelationEntity> entityList) {
        return templatePropertyRelationDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<TemplatePropertyRelationEntity> entityList, int batchSize) {
        return templatePropertyRelationDao.saveBatch(entityList,batchSize);
    }


    public void updateById(TemplatePropertyRelationEntity entity) {
        if(!templatePropertyRelationDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    public void update(TemplatePropertyRelationEntity entity, Wrapper<TemplatePropertyRelationEntity> updateWrapper) {
        if(!templatePropertyRelationDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<TemplatePropertyRelationEntity> entityList) {
        return templatePropertyRelationDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<TemplatePropertyRelationEntity> entityList, int batchSize) {
        return templatePropertyRelationDao.updateBatchById(entityList,batchSize);
    }


    public TemplatePropertyRelationEntity selectById(Serializable id) {
        return templatePropertyRelationDao.getById(id);
    }


    public void deleteById(Serializable id) {
        if(!templatePropertyRelationDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            boolean result = templatePropertyRelationDao.removeByIds(idList);
            if (!result){
                throw new CurdException(RespCode.DELETE_ERROR);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return templatePropertyRelationDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<TemplatePropertyRelationEntity> queryWrapper) {
        return templatePropertyRelationDao.remove(queryWrapper);
    }



}