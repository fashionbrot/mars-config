package com.github.fashionbrot.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.enums.SystemConfigRoleEnum;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.dao.entity.SystemConfigInfo;
import com.github.fashionbrot.dao.entity.SystemConfigRoleRelation;
import com.github.fashionbrot.dao.mapper.SystemConfigRoleRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Service
public class SystemConfigRoleRelationDao {

    @Autowired
    private SystemConfigRoleRelationMapper systemConfigRoleRelationMapper;

    @Autowired
    private SystemConfigDao systemConfigDao;
    @Autowired
    private UserInfoDao userInfoDao;


    public int add(SystemConfigRoleRelation systemConfigRoleRelation) {
        return systemConfigRoleRelationMapper.insert(systemConfigRoleRelation);
    }


    public Integer update(SystemConfigRoleRelation systemConfigRoleRelation) {
        return systemConfigRoleRelationMapper.updateById(systemConfigRoleRelation);
    }


    public Integer deleteById(Long id) {
        return systemConfigRoleRelationMapper.deleteById(id);
    }


    public SystemConfigRoleRelation queryById(Long id) {
        return systemConfigRoleRelationMapper.selectById(id);
    }


    public List<SystemConfigRoleRelation> queryAll() {
        return systemConfigRoleRelationMapper.selectList(null);
    }


    public List<SystemConfigRoleRelation> selectBy(SystemConfigInfo info) {
        return systemConfigRoleRelationMapper.selectBy(info.getEnvCode(),info.getAppName(),info.getRoleId());
    }


    @Transactional(rollbackFor = Exception.class)
    public int syncRole(SystemConfigInfo systemConfigInfo) {
        List<SystemConfigRoleRelation> systemConfigInfoList = selectBy(systemConfigInfo);
        List<Long> ids =null;
        if (!CollectionUtils.isEmpty(systemConfigInfoList)) {
            ids = systemConfigInfoList.stream().map(SystemConfigRoleRelation::getSystemConfigId).collect(Collectors.toList());
        }
        QueryWrapper queryWrapper = new QueryWrapper<SystemConfigInfo>();
        queryWrapper.eq("env_code",systemConfigInfo.getEnvCode());
        queryWrapper.eq("app_name",systemConfigInfo.getAppName());
        if (!CollectionUtils.isEmpty(ids)) {
            queryWrapper.notIn("id", ids);
        }
        List<SystemConfigInfo> list = systemConfigDao.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)){
            return 1;
        }
        for (SystemConfigInfo info  : list){
            systemConfigRoleRelationMapper.insert(SystemConfigRoleRelation.builder()
                    .roleId(systemConfigInfo.getRoleId())
                    .systemConfigId(info.getId())
                    .viewStatus((byte) 1)
                    .build());
        }

        return 0;
    }


    @Transactional(rollbackFor = Exception.class)
    public Integer saveRole(List<SystemConfigRoleRelation> relations) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(relations)){
            throw new MarsException("请选择要修改的数据");
        }
        Date date = new Date();
        if (!CollectionUtils.isEmpty(relations)){
            for (SystemConfigRoleRelation relation:relations){
                relation.setUpdateDate(date);
                systemConfigRoleRelationMapper.update(relation,new QueryWrapper<SystemConfigRoleRelation>().eq("id",relation.getId()));
            }
        }
        return 1;
    }

    public void checkRole(Long systemConfigId, SystemConfigRoleEnum configRoleEnum){
        LoginModel model  =userInfoDao.getLogin();
        if (model!=null && model.isSuperAdmin()){
            return;
        }
        SystemConfigRoleRelation systemConfigRoleRelation = systemConfigRoleRelationMapper.selectByRole(systemConfigId,model.getUserId());
        if (systemConfigRoleRelation!=null){
            if (configRoleEnum == SystemConfigRoleEnum.VIEW && systemConfigRoleRelation.getViewStatus()!=1){
                throw new MarsException(RespCode.NOT_PERMISSION_ERROR);
            }else if (configRoleEnum == SystemConfigRoleEnum.EDIT && systemConfigRoleRelation.getEditStatus()!=1){
                throw new MarsException(RespCode.NOT_PERMISSION_ERROR);
            }else if (configRoleEnum == SystemConfigRoleEnum.PUSH && systemConfigRoleRelation.getPushStatus()!=1){
                throw new MarsException(RespCode.NOT_PERMISSION_ERROR);
            }else if (configRoleEnum == SystemConfigRoleEnum.DELETE && systemConfigRoleRelation.getDeleteStatus()!=1){
                throw new MarsException(RespCode.NOT_PERMISSION_ERROR);
            }
        }else{
            throw new MarsException(RespCode.NOT_PERMISSION_ERROR);
        }
    }


    public int delete(QueryWrapper queryWrapper) {
        return systemConfigRoleRelationMapper.delete(queryWrapper);
    }

}
