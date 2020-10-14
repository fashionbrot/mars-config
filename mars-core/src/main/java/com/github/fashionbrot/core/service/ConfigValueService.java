package com.github.fashionbrot.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.core.UserLoginService;
import com.github.fashionbrot.dao.dao.ConfigValueDao;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 配置数据表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@Service
public class ConfigValueService  {

    @Autowired
    private ConfigValueDao configValueDao;

    @Autowired
    private UserLoginService userLoginService;


    public Collection<ConfigValueEntity> queryList(Map<String, Object> params) {
        return configValueDao.listByMap(params);
    }

    /**
     * 查询数据列表
     * @param params 查询条件
     * @return List<ConfigValueEntity>
     */
    public Collection<ConfigValueEntity> configValueByMap(Map<String, Object> params){
        return configValueDao.listByMap(params);
    }

    /**
    * 分页查询
    * @param req
    * @return
    */
    public PageVo pageList(ConfigValueReq req){
        Page<?> page= PageHelper.startPage(req.getPage(),req.getPageSize());
        QueryWrapper<ConfigValueEntity> queryWrapper=new QueryWrapper();
        queryWrapper.eq("app_name",req.getAppName());
        queryWrapper.eq("env_code",req.getEnvCode());
        queryWrapper.eq("template_key",req.getTemplateKey());
        if (StringUtils.isNotEmpty(req.getDescription())){
            queryWrapper.like("description",req.getDescription());
        }
        List<ConfigValueEntity> list = configValueDao.list(queryWrapper);

        return PageVo.builder()
                .data(list)
                .iTotalDisplayRecords(page.getTotal())
                .build();
    }



    public void insert(ConfigValueEntity entity) {
        setDate(entity);
        if(!configValueDao.save(entity)){
            throw new CurdException(RespCode.SAVE_ERROR);
        }
    }

    private void setDate(ConfigValueEntity entity) {
        LoginModel model = userLoginService.getLogin();
        if (model!=null){

            entity.setUserName(new String(Base64.getDecoder().decode(model.getUserName())));
        }
        String json = entity.getJson();
        if (StringUtils.isEmpty(json)){
            throw new MarsException("请配置模板属性");
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(json);
        }catch (Exception e){
            throw new MarsException("填写属性值格式有误，请检查");
        }
        if (jsonObject!=null && jsonObject.containsKey("startDate") && StringUtils.isNotEmpty(jsonObject.getString("startDate"))){
            try {
                entity.setStartTime(DateUtils.parseDate(jsonObject.getString("startDate"),"yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject!=null && jsonObject.containsKey("endDate") && StringUtils.isNotEmpty(jsonObject.getString("endDate"))){
            try {
                entity.setEndTime(DateUtils.parseDate(jsonObject.getString("endDate"),"yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<ConfigValueEntity> entityList) {
        return configValueDao.saveBatch(entityList,30);
    }

    /**
     * 批量插入
     *
     * @param entityList
     * @param batchSize
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(Collection<ConfigValueEntity> entityList, int batchSize) {
        return configValueDao.saveBatch(entityList,batchSize);
    }


    public void updateById(ConfigValueEntity entity) {
        setDate(entity);
        if(!configValueDao.updateById(entity)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    public void update(ConfigValueEntity entity, Wrapper<ConfigValueEntity> updateWrapper) {
        if(!configValueDao.update(entity, updateWrapper)){
            throw new CurdException(RespCode.UPDATE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<ConfigValueEntity> entityList) {
        return configValueDao.updateBatchById(entityList,30);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchById(Collection<ConfigValueEntity> entityList, int batchSize) {
        return configValueDao.updateBatchById(entityList,batchSize);
    }


    public ConfigValueEntity selectById(Serializable id) {
        return configValueDao.getById(id);
    }


    public void deleteById(Serializable id) {
        if(!configValueDao.removeById(id)){
            throw new CurdException(RespCode.DELETE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isNotEmpty(idList)){
            boolean result = configValueDao.removeByIds(idList);
            if (!result){
                throw new CurdException(RespCode.DELETE_ERROR);
            }
        }
    }


    public boolean deleteByMap(Map<String, Object> columnMap) {
        return configValueDao.removeByMap(columnMap);
    }


    public boolean delete(Wrapper<ConfigValueEntity> queryWrapper) {
        return configValueDao.remove(queryWrapper);
    }



}