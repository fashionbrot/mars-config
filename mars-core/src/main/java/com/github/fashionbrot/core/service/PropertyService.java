package com.github.fashionbrot.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.req.PropertyReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.dao.dao.PropertyDao;
import com.github.fashionbrot.dao.entity.PropertyEntity;
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
 * 属性表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */
@Service
public class PropertyService  {

    @Autowired
    private PropertyDao propertyDao;


    public Collection<PropertyEntity> queryList(Map<String, Object> params) {
        PageHelper.orderBy("priority asc");
        return propertyDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<PropertyEntity>
     */
    public Collection<PropertyEntity> propertyByMap(Map<String, Object> params){
        return propertyDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(PropertyReq req){
        Page<?> page= PageHelper.startPage(req.getPage(),req.getPageSize());
        QueryWrapper<PropertyEntity> queryWrapper=new QueryWrapper();
        if (StringUtils.isNotEmpty(req.getAppName())){
            queryWrapper.eq("app_name",req.getAppName());
        }
        queryWrapper.eq("template_key",req.getTemplateKey());
        if (StringUtils.isNotEmpty(req.getPropertyKey())){
            queryWrapper.like("property_key",req.getPropertyKey());
        }
        queryWrapper.orderByAsc("priority");
        List<PropertyEntity> list = propertyDao.list(queryWrapper);

        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }



    public void insert(PropertyEntity entity) {
        if(!propertyDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<PropertyEntity> entityList) {
        return propertyDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<PropertyEntity> entityList, int batchSize) {
        return propertyDao.saveBatch(entityList,batchSize);
    }


    public void updateById(PropertyEntity entity) {
        if(!propertyDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    public void update(PropertyEntity entity, Wrapper<PropertyEntity> updateWrapper) {
        if(!propertyDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<PropertyEntity> entityList) {
        return propertyDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<PropertyEntity> entityList, int batchSize) {
        return propertyDao.updateBatchById(entityList,batchSize);
    }


    public PropertyEntity selectById(Serializable id) {
        return propertyDao.getById(id);
    }


    public void deleteById(Serializable id) {
        if(!propertyDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            boolean result = propertyDao.removeByIds(idList);
            if (!result){
                throw new CurdException(RespCode.DELETE_ERROR);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return propertyDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<PropertyEntity> queryWrapper) {
        return propertyDao.remove(queryWrapper);
    }



}