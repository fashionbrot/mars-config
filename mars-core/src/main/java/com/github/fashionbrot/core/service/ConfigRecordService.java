package com.github.fashionbrot.core.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.fashionbrot.common.enums.OperationTypeEnum;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.req.ConfigRecordReq;
import com.github.fashionbrot.common.vo.PageVo;
import com.github.fashionbrot.core.UserLoginService;
import com.github.fashionbrot.dao.dao.ConfigRecordDao;
import com.github.fashionbrot.dao.dao.ConfigValueDao;
import com.github.fashionbrot.dao.dao.PropertyDao;
import com.github.fashionbrot.dao.dao.TableColumnDao;
import com.github.fashionbrot.dao.entity.ConfigRecordEntity;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import com.github.fashionbrot.dao.mapper.TableColumnMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ConfigRecordService {

    @Autowired
    private ConfigRecordDao configRecordDao;

    @Autowired
    private ConfigValueDao configValueDao;

    @Autowired
    private TableColumnDao tableColumnDao;

    @Autowired
    private UserLoginService userLoginService;


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
        QueryWrapper<ConfigRecordEntity> q =new QueryWrapper();
        q.eq("env_code",req.getEnvCode());
        q.eq("app_name",req.getAppName());
        String templateKey=req.getTemplateKey();
        if (StringUtils.isNotEmpty(templateKey)) {
            q.eq("template_key", templateKey);
        }
        q.orderByDesc("id");
        List<ConfigRecordEntity> list = configRecordDao.list(q);

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
        ConfigRecordEntity configRecordEntity=  configRecordDao.getById(id);
        if (configRecordEntity!=null){

        }
        return configRecordEntity;
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


    @Transactional(rollbackFor = Exception.class)
    public void rollBack(ConfigRecordReq req) {
        ConfigRecordEntity byId = configRecordDao.getById(req.getId());
        if (byId==null){
            throw new MarsException("记录不存在");
        }

        ConfigValueEntity valueEntity = configValueDao.getById(byId.getConfigId());
        if (valueEntity==null){
            throw new MarsException("配置信息已不存在");
        }
        String json = byId.getJson();
        ConfigValueEntity newEntity = JSON.parseObject(json,ConfigValueEntity.class);
        if (newEntity==null){
            throw new MarsException("配置信息回滚失败，请联系管理员");
        }

        ConfigValueEntity update=ConfigValueEntity.builder().build();
        update.setStatus(newEntity.getStatus());
        update.setDescription(newEntity.getDescription());
        configValueDao.update(update,new QueryWrapper<ConfigValueEntity>().eq("id",byId.getConfigId()));


        String sql= tableColumnDao.getUpdateConfigSql(newEntity);
        log.info("rollback sql:"+sql);
        tableColumnDao.updateTable(sql);

        LoginModel login = userLoginService.getLogin();

        ConfigRecordEntity record=ConfigRecordEntity.builder()
                .appName(valueEntity.getAppName())
                .envCode(valueEntity.getEnvCode())
                .templateKey(valueEntity.getTemplateKey())
                .configId(valueEntity.getId())
                .json(byId.getNewJson())
                .newJson(byId.getJson())
                .operationType(OperationTypeEnum.ROLLBACK.getCode())
                .description(valueEntity.getDescription())
                .userName(login.getUserName())
                .build();
        configRecordDao.save(record);
    }
}