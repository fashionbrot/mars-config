package com.github.fashionbrot.core.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.req.EnvVariableReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.dao.dao.EnvVariableDao;
import com.github.fashionbrot.dao.dao.EnvVariableRelationDao;
import com.github.fashionbrot.dao.dao.PropertyDao;
import com.github.fashionbrot.dao.entity.EnvVariableEntity;
import com.github.fashionbrot.dao.entity.EnvVariableRelationEntity;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 常量表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@Service
public class EnvVariableService {

    @Autowired
    private EnvVariableDao envVariableDao;
    @Autowired
    private EnvVariableRelationDao envVariableRelationDao;

    @Autowired
    private PropertyDao propertyDao;


    public Collection<EnvVariableEntity> queryList(Map<String, Object> params) {
        return envVariableDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<EnvVariableEntity>
     */
    public Collection<EnvVariableEntity> envVariableByMap(Map<String, Object> params){
        return envVariableDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(EnvVariableReq req){
        Page<?> page= PageHelper.startPage((req.getStart() / req.getLength()) + 1, req.getLength());
        List<EnvVariableEntity> list = envVariableDao.list(null);

        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }



    @Transactional(rollbackFor = Exception.class)
    public void insert(EnvVariableEntity entity) {
        if (envVariableDao.count(new QueryWrapper<EnvVariableEntity>().eq("variable_key",entity.getVariableKey()))>0){
            throw new CurdException("当前key 已存在，请重新输入");
        }
        if(!envVariableDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }

        addRelation(entity,true);
    }

    private void addRelation(EnvVariableEntity envInfo,boolean flag) {
        String json=envInfo.getRelation();
        List<EnvVariableRelationEntity> envVariableRelationList= JSONObject.parseArray(json, EnvVariableRelationEntity.class);
        if (CollectionUtils.isNotEmpty(envVariableRelationList)){
            for(EnvVariableRelationEntity relation:envVariableRelationList){
                if (flag){
                    relation.setVariableKey(envInfo.getVariableKey());
                }
                envVariableRelationDao.save(relation);
            }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<EnvVariableEntity> entityList) {
        return envVariableDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<EnvVariableEntity> entityList, int batchSize) {
        return envVariableDao.saveBatch(entityList,batchSize);
    }


    public void updateById(EnvVariableEntity entity) {
        EnvVariableEntity envVariable=envVariableDao.getOne(new QueryWrapper<EnvVariableEntity>().eq("variable_name",entity.getVariableName()));
        if (envVariable!=null && entity.getId()!=envVariable.getId()){
            throw new CurdException("变量名称已存在");
        }

        envVariableRelationDao.remove(new QueryWrapper<EnvVariableRelationEntity>().eq("variable_key",entity.getVariableKey()));
        addRelation(entity,false);

        if(!envVariableDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    public void update(EnvVariableEntity entity, Wrapper<EnvVariableEntity> updateWrapper) {
        if(!envVariableDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<EnvVariableEntity> entityList) {
        return envVariableDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<EnvVariableEntity> entityList, int batchSize) {
        return envVariableDao.updateBatchById(entityList,batchSize);
    }


    public EnvVariableEntity selectById(Serializable id) {
        return envVariableDao.getById(id);
    }


    public void deleteById(Serializable id) {
        EnvVariableEntity envVariable= envVariableDao.getById(id);
        if (envVariable!=null){
            int count=propertyDao.count(new QueryWrapper<PropertyEntity>().eq("variable_key",envVariable.getVariableKey()));
            if(count!=0){
               throw new CurdException("当前变量已在属性中使用，不能删除");
            }
            QueryWrapper queryWrapper=new QueryWrapper<EnvVariableRelationEntity>();
            queryWrapper.eq("variable_key",envVariable.getVariableKey());
            envVariableRelationDao.remove(queryWrapper);
        }
        if(!envVariableDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            boolean result = envVariableDao.removeByIds(idList);
            if (!result){
                throw new CurdException(RespCode.DELETE_ERROR);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return envVariableDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<EnvVariableEntity> queryWrapper) {
        return envVariableDao.remove(queryWrapper);
    }



}