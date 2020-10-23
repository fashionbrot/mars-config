package com.github.fashionbrot.core.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.req.TemplateReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.dao.dao.TemplateDao;
import com.github.fashionbrot.dao.dao.TemplatePropertyRelationDao;
import com.github.fashionbrot.dao.entity.TemplateEntity;
import com.github.fashionbrot.dao.mapper.TableColumnMapper;
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
 * 模板表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */
@Service
public class TemplateService {

    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private TableColumnMapper tableColumnMapper;


    public Collection<TemplateEntity> queryList(Map<String, Object> params) {
        return templateDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<TemplateEntity>
     */
    public Collection<TemplateEntity> templateByMap(Map<String, Object> params){
        return templateDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(TemplateReq req){
        Page<?> page= PageHelper.startPage(req.getPage(),req.getPageSize());
        QueryWrapper queryWrapper=new QueryWrapper();
        if (StringUtils.isNotEmpty(req.getAppName())){
            queryWrapper.eq("app_name",req.getAppName());
        }
        List<TemplateEntity> list = templateDao.list(queryWrapper);
        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }

    public Object list(TemplateReq req) {
        QueryWrapper queryWrapper=new QueryWrapper();
        if (StringUtils.isNotEmpty(req.getAppName()) && !"-1".equals(req.getAppName())){
            queryWrapper.eq("app_name",req.getAppName());
        }
        return templateDao.list(queryWrapper);
    }



    @Transactional(rollbackFor = Exception.class)
    public void insert(TemplateEntity entity) {
        if(templateDao.count(new QueryWrapper<TemplateEntity>()
                .eq("template_key",entity.getTemplateKey())
                .eq("app_name",entity.getAppName())
        )>0){
            throw new MarsException("模板已存在");
        }
        if(!templateDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }
        tableColumnMapper.addTable(entity.getAppName()+"_"+entity.getTemplateKey());
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<TemplateEntity> entityList) {
        return templateDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<TemplateEntity> entityList, int batchSize) {
        return templateDao.saveBatch(entityList,batchSize);
    }


    public void updateById(TemplateEntity entity) {

        if(!templateDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    public void update(TemplateEntity entity, Wrapper<TemplateEntity> updateWrapper) {
        if(!templateDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<TemplateEntity> entityList) {
        return templateDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<TemplateEntity> entityList, int batchSize) {
        return templateDao.updateBatchById(entityList,batchSize);
    }


    public TemplateEntity selectById(Serializable id) {
        return templateDao.getById(id);
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Serializable id) {
        TemplateEntity byId = templateDao.getById(id);
        if (byId==null){
            throw new MarsException("模板不存在");
        }
        if(!templateDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }
        tableColumnMapper.dropTable(byId.getAppName()+"_"+byId.getTemplateKey());
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            boolean result = templateDao.removeByIds(idList);
            if (!result){
                throw new CurdException(RespCode.DELETE_ERROR);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return templateDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<TemplateEntity> queryWrapper) {
        return templateDao.remove(queryWrapper);
    }



}