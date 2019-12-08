package com.gitee.mars.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.enums.SystemConfigRoleEnum;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.common.req.DataConfigReq;
import com.gitee.mars.common.vo.CheckForUpdateVo;
import com.gitee.mars.common.vo.ForDataVo;
import com.gitee.mars.common.vo.PageDataVo;
import com.gitee.mars.dao.dao.SystemConfigDao;
import com.gitee.mars.dao.dao.SystemConfigHistoryDao;
import com.gitee.mars.dao.dao.SystemConfigRoleRelationDao;
import com.gitee.mars.dao.entity.SystemConfigHistoryInfo;
import com.gitee.mars.dao.entity.SystemConfigInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SystemConfigService {
    @Autowired
    private SystemConfigDao systemConfigService;

    @Autowired
    private SystemConfigHistoryDao systemConfigHistoryDao;

    @Autowired
    private SystemConfigRoleRelationDao systemConfigRoleRelationService;


    public void add(SystemConfigInfo systemConfigInfo) {
        try {
            if ("yaml".equalsIgnoreCase(systemConfigInfo.getFileType())) {
                //TODO 需要验证
//                ConfigParseUtils.toProperties(systemConfigInfo.getJson(), systemConfigInfo.getFileType());
            }
        } catch (Exception e) {
            log.error(" add error", e);
            throw new MarsException("填写格式错误,请检查输入格式（ymal填写时不能有 TAB 换行）");
        }
        if (systemConfigService.add(systemConfigInfo) != 1) {
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }


    public void update(SystemConfigInfo systemConfigInfo) {
        if (systemConfigService.update(systemConfigInfo) != 1) {
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        if (systemConfigService.deleteById(id) != 1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public SystemConfigInfo queryById(Long id) {
        return systemConfigService.queryById(id);
    }


    public List<SystemConfigInfo> queryAll() {
        return systemConfigService.queryAll(null);
    }


    public List<SystemConfigInfo> queryByAppAndEnv(String appName, String envCode) {
        return systemConfigService.queryByAppAndEnv(appName, envCode);
    }


    public SystemConfigHistoryInfo queryHistoryById(Long fileId) {
        SystemConfigHistoryInfo systemConfigHistoryInfo = systemConfigHistoryDao.selectById(fileId);
        systemConfigRoleRelationService.checkRole(systemConfigHistoryInfo.getFileId(), SystemConfigRoleEnum.VIEW);
        return systemConfigHistoryInfo;
    }


    public void rollBackById(Long id) {
        if (systemConfigService.rollBackById(id) != 1) {
            throw new MarsException(RespCode.ROLL_BACK_ERROR);
        }
    }


    public RespCode publish(Long id) {
        systemConfigService.publish(id);
        return RespCode.SUCCESS;
    }


    public PageDataVo<SystemConfigHistoryInfo> queryHistoryAll(SystemConfigHistoryInfo info, Integer start, Integer length) {

        QueryWrapper<SystemConfigHistoryInfo> queryWrapper = new QueryWrapper();
        if (info != null) {
            if (StringUtils.isNotEmpty(info.getEnvCode())) {
                queryWrapper.eq("env_code", info.getEnvCode());
            }
            if (StringUtils.isNotEmpty(info.getAppName())) {
                queryWrapper.eq("app_name", info.getAppName());
            }
        }
        queryWrapper.select("id,file_id,file_name,modifier,update_date,app_name,env_code,operation_type");
        queryWrapper.orderByDesc("id");
        Page pageInfo;
        if (start != null && length != null) {
            pageInfo = PageHelper.startPage((start / length) + 1, length);
        } else {
            pageInfo = PageHelper.startPage(1, 20);
        }
        List<SystemConfigHistoryInfo> systemConfigHistoryInfos = systemConfigHistoryDao.selectList(queryWrapper);

        PageDataVo<SystemConfigHistoryInfo> pageData = new PageDataVo<SystemConfigHistoryInfo>();
        pageData.setData(systemConfigHistoryInfos);
        pageData.setITotalDisplayRecords(pageInfo.getTotal());

        return pageData;
    }


    public void deleteHistoryById(Long id) {
        SystemConfigHistoryInfo systemConfigHistoryInfo = systemConfigHistoryDao.selectById(id);
        if (systemConfigHistoryInfo != null) {
            systemConfigRoleRelationService.checkRole(systemConfigHistoryInfo.getFileId(), SystemConfigRoleEnum.VIEW);
        }
        if (systemConfigHistoryDao.deleteById(id) != 1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public CheckForUpdateVo checkForUpdate(DataConfigReq dataConfig) {
        if (dataConfig != null && StringUtils.isEmpty(dataConfig.getToken())) {
            return null;
        }
        /*MD5 md5 = MD5.getInstance();
        String token =md5.getMD5String(getToken(dataConfig.getEnvCode(),dataConfig.getAppId(),dataConfig.getVersion()));
        if (!token.equals(dataConfig.getToken())){
            return null;
        }*/
        return systemConfigService.checkForUpdate(dataConfig);
    }


    public ForDataVo forDataVo(DataConfigReq dataConfig) {
        if (dataConfig != null && StringUtils.isEmpty(dataConfig.getToken())) {
            return null;
        }
        /*MD5 md5 = MD5.getInstance();
        String token =md5.getMD5String(getToken(dataConfig.getEnvCode(),dataConfig.getAppId(),dataConfig.getFileName(),dataConfig.getVersion()));
        if (!token.equals(dataConfig.getToken())){
            return null;
        }*/

        return systemConfigService.forDataVo(dataConfig);
    }
}