package com.gitee.mars.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.enums.SystemConfigRoleEnum;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.dao.entity.SystemConfigInfo;
import com.gitee.mars.dao.entity.SystemConfigRoleRelation;
import com.gitee.mars.dao.mapper.SystemConfigRoleRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemConfigRoleRelationDao {

    @Autowired
    private SystemConfigRoleRelationMapper systemConfigRoleRelationMapper;

    @Autowired
    private SystemConfigDao systemConfigDao;
    @Autowired
    private UserInfoDao userInfoService;


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
        List<SystemConfigInfo> list = systemConfigDao.queryAll(queryWrapper);
        if (CollectionUtils.isEmpty(list)){
            return 1;
        }
        Date date = new Date();
        for (SystemConfigInfo info  : list){
            systemConfigRoleRelationMapper.insert(SystemConfigRoleRelation.builder()
                    .roleId(systemConfigInfo.getRoleId())
                    .systemConfigId(info.getId())
                    .viewStatus((byte) 1)
                    .createDate(date)
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
        Long  userId  =userInfoService.getUserId();
        if (userId==null){
            return;
        }
        SystemConfigRoleRelation systemConfigRoleRelation = systemConfigRoleRelationMapper.selectByRole(systemConfigId,userId);
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
        }
    }


    public int delete(QueryWrapper queryWrapper) {
        return systemConfigRoleRelationMapper.delete(queryWrapper);
    }

}
