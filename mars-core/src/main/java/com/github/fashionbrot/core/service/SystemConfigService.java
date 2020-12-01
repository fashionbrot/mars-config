package com.github.fashionbrot.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.constant.MarsConst;
import com.github.fashionbrot.common.enums.OperationTypeEnum;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.enums.SystemConfigRoleEnum;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.req.DataConfigReq;
import com.github.fashionbrot.common.vo.CheckForUpdateVo;
import com.github.fashionbrot.common.vo.ForDataVo;
import com.github.fashionbrot.common.vo.PageDataVo;
import com.github.fashionbrot.core.UserLoginService;
import com.github.fashionbrot.dao.dao.SystemConfigDao;
import com.github.fashionbrot.dao.dao.SystemConfigHistoryDao;
import com.github.fashionbrot.dao.dao.SystemConfigRoleRelationDao;
import com.github.fashionbrot.dao.dao.SystemReleaseDao;
import com.github.fashionbrot.dao.entity.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Service
@Slf4j
public class SystemConfigService {
    @Autowired
    private SystemConfigDao systemConfigDao;

    @Autowired
    private SystemConfigHistoryDao systemConfigHistoryDao;

    @Autowired
    private SystemConfigRoleRelationDao systemConfigRoleRelationDao;

    @Autowired
    private SystemReleaseDao systemReleaseDao;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private SystemConfigCacheService systemConfigCacheService;


    private boolean isFileHasExisted(SystemConfigInfo systemConfigInfo) {
        Integer count = systemConfigDao.count(new QueryWrapper<SystemConfigInfo>().
                eq("env_code", systemConfigInfo.getEnvCode()).
                eq("app_name", systemConfigInfo.getAppName()).
                eq("file_name", systemConfigInfo.getFileName() + "." + systemConfigInfo.getFileType()));
        if (count > 0) {
            return true;
        }
        return false;
    }

    public void updateRelease(String envCode,String appName,String fileName){
        QueryWrapper q = new QueryWrapper<ConfigReleaseEntity>()
                .eq("env_code",envCode)
                .eq("app_name",appName)
                .eq("release_flag",0);
        SystemReleaseEntity releaseEntity = systemReleaseDao.getOne(q);
        if (releaseEntity==null){
            releaseEntity = SystemReleaseEntity.builder()
                    .envCode(envCode)
                    .appName(appName)
                    .files(fileName)
                    .releaseFlag(0)
                    .build();
            if(!systemReleaseDao.save(releaseEntity)){
                throw new CurdException(RespCode.FAIL);
            }
        }else{

            String oldKeys = releaseEntity.getFiles();
            if (StringUtils.isNotEmpty(oldKeys)){
                oldKeys=oldKeys+","+fileName;
                List<String> keys = Arrays.stream(oldKeys.split(",")).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(keys)){
                    oldKeys = String.join(",",keys);
                }
            }else{
                oldKeys = fileName;
            }

            SystemReleaseEntity update= new SystemReleaseEntity();
            update.setFiles(oldKeys);
            if(!systemReleaseDao.update(update,q)){
                throw new CurdException(RespCode.FAIL);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(SystemConfigInfo systemConfigInfo) {
        try {
            if ("yaml".equalsIgnoreCase(systemConfigInfo.getFileType())) {
                //TODO 需要验证 数据格式
            }
        } catch (Exception e) {
            log.error(" add error", e);
            throw new MarsException("填写格式错误,请检查输入格式（ymal填写时不能有 TAB 换行）");
        }

        if (isFileHasExisted(systemConfigInfo)) {
            throw new MarsException("配置文件已存在");
        }
        LoginModel login = userLoginService.getLogin();
        if (login != null) {
            systemConfigInfo.setModifier(login.getUserName());
        }
        systemConfigDao.save(systemConfigInfo);
        updateRelease(systemConfigInfo.getEnvCode(),systemConfigInfo.getAppName(),systemConfigInfo.getFileName());
    }


    @Transactional(rollbackFor = Exception.class)
    public void update(SystemConfigInfo systemConfigInfo) {

        systemConfigRoleRelationDao.checkRole(systemConfigInfo.getId(), SystemConfigRoleEnum.EDIT);

        SystemConfigInfo systemConfigInfoPre = systemConfigDao.getById(systemConfigInfo.getId());
        if (systemConfigInfoPre == null) {
            throw new MarsException(RespCode.EXIST_SYSTEM_CONFIG_ERROR);
        }

        //TODO 需要验证
         /*try {
         if ("yaml".equalsIgnoreCase(systemConfigInfo.getFileType())) {
         }
         }catch (Exception e){
         log.error(" update parse content error",e);
         throw new MarsException("填写格式错误,请检查输入格式（ymal填写时不能有 TAB 换行）");
         }*/
        String json = systemConfigInfoPre.getJson();
        int flag = 0;
        String preFileJson = systemConfigInfoPre.getJson();
        if (OperationTypeEnum.UPDATE == OperationTypeEnum.ROLLBACK) {
            flag = systemConfigInfoPre.getJson().compareTo(json);
            preFileJson = json;
        } else {
            flag = systemConfigInfoPre.getJson().compareTo(systemConfigInfo.getJson());
        }
        if (flag != 0) {
            systemConfigInfoPre.setStatus(0);
        }

        LoginModel userInfo = userLoginService.getLogin();
        if (userInfo != null) {
            systemConfigInfoPre.setModifier(userInfo.getUserName());

            systemConfigInfoPre.setFileDesc(systemConfigInfo.getFileDesc());
            systemConfigInfoPre.setJson(systemConfigInfo.getJson());
            systemConfigInfoPre.setFileType(systemConfigInfo.getFileType());

            QueryWrapper updateWrapper = new QueryWrapper<SystemConfigInfo>();
            updateWrapper.eq("id", systemConfigInfoPre.getId());

            boolean result = systemConfigDao.update(systemConfigInfoPre, updateWrapper);
            //判断乐观锁
            if (!result) {
                throw new MarsException(RespCode.UPDATE_REFRESH_ERROR);
            }
            if (flag != 0) {
                systemConfigHistoryDao.insert(generateHistoryInfo(systemConfigInfoPre, preFileJson,OperationTypeEnum.UPDATE));
            }
        }

        updateRelease(systemConfigInfoPre.getEnvCode(),systemConfigInfoPre.getAppName(),systemConfigInfoPre.getFileName());

    }

    private SystemConfigHistoryInfo generateHistoryInfo(SystemConfigInfo systemConfigInfo, String preFileJson, OperationTypeEnum operationTypeEnum) {
        SystemConfigHistoryInfo systemConfigHistoryInfo = new SystemConfigHistoryInfo();
        BeanUtils.copyProperties(systemConfigInfo, systemConfigHistoryInfo, "id");
        systemConfigHistoryInfo.setFileId(systemConfigInfo.getId());
        systemConfigHistoryInfo.setPreJson(preFileJson);
        systemConfigHistoryInfo.setOperationType(operationTypeEnum.getCode());
        return systemConfigHistoryInfo;
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {

        systemConfigRoleRelationDao.checkRole(id, SystemConfigRoleEnum.DELETE);

        SystemConfigInfo systemConfigInfo = systemConfigDao.getById(id);
        String preJson = systemConfigInfo.getJson();

        LoginModel login = userLoginService.getLogin();
        if (login!=null){
            systemConfigInfo.setModifier(login.getUserName());
        }
        systemConfigInfo.setJson("");

        systemConfigHistoryDao.insert(generateHistoryInfo(systemConfigInfo, preJson, OperationTypeEnum.DELETE));
        systemConfigRoleRelationDao.delete(new QueryWrapper<SystemConfigRoleRelation>().eq("system_config_id", id));
        systemConfigDao.removeById(id);
    }


    public SystemConfigInfo queryById(Long id) {
        systemConfigRoleRelationDao.checkRole(id, SystemConfigRoleEnum.VIEW);

        SystemConfigInfo systemConfigInfo =  systemConfigDao.getById(id);
        if (systemConfigInfo!=null){
            systemConfigInfo.setNowUpdateDate(systemConfigInfo.getUpdateDate()!=null?systemConfigInfo.getUpdateDate().getTime():null);
        }
        return systemConfigInfo;
    }


    public List<SystemConfigInfo> queryAll() {
        return systemConfigDao.list(null);
    }


    public List<SystemConfigInfo> queryByAppAndEnv(String appName, String envCode) {
        QueryWrapper queryWrapper = new QueryWrapper<SystemConfigInfo>().
                eq("app_name", appName).
                eq("env_code", envCode);
        queryWrapper.select("id,file_type,env_code,app_name,file_name,file_desc,modifier,update_date,status");
        queryWrapper.orderByDesc("id");
        return systemConfigDao.list(queryWrapper);
    }


    public SystemConfigHistoryInfo queryHistoryById(Long fileId) {
        SystemConfigHistoryInfo systemConfigHistoryInfo = systemConfigHistoryDao.selectById(fileId);
        if (systemConfigHistoryInfo!=null) {
            systemConfigRoleRelationDao.checkRole(systemConfigHistoryInfo.getFileId(), SystemConfigRoleEnum.VIEW);
        }
        return systemConfigHistoryInfo;
    }


    @Transactional(rollbackFor = Exception.class)
    public void rollBackById(Long id) {

        SystemConfigHistoryInfo systemConfigHistoryInfo = systemConfigHistoryDao.selectById(id);
        if (systemConfigHistoryInfo == null) {
            throw new MarsException(RespCode.HISTORY_CONFIG_NOT_EXIST);
        }
        SystemConfigInfo systemConfigInfo = systemConfigDao.getById(systemConfigHistoryInfo.getFileId());
        if (systemConfigInfo != null) {
            systemConfigRoleRelationDao.checkRole(systemConfigHistoryInfo.getFileId(), SystemConfigRoleEnum.EDIT);
            systemConfigInfo.setJson(systemConfigHistoryInfo.getPreJson());
            systemConfigDao.updateById(systemConfigInfo);

        } else {
            SystemConfigInfo configInfo = SystemConfigInfo.builder()
                    .appName(systemConfigHistoryInfo.getAppName())
                    .envCode(systemConfigHistoryInfo.getEnvCode())
                    .fileType(systemConfigHistoryInfo.getFileType())
                    .fileName(systemConfigHistoryInfo.getFileName())
                    .status(0)
                    .json(systemConfigHistoryInfo.getPreJson())
                    .build();
            systemConfigDao.save(configInfo);
        }
    }


    public RespCode publish(Long id) {

        systemConfigRoleRelationDao.checkRole(id, SystemConfigRoleEnum.PUSH);

        SystemConfigInfo systemConfigInfo = systemConfigDao.getById(id);
        if (systemConfigInfo != null && systemConfigInfo.getStatus() == 1) {
            throw new MarsException("已发布");
        }
        systemConfigInfo.setStatus(1);
        boolean result = systemConfigDao.updateById(systemConfigInfo);
        if (!result) {
            throw new MarsException(RespCode.PUBLISH_ERROR);
        }
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
            pageInfo = PageHelper.startPage(start , length);
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
            systemConfigRoleRelationDao.checkRole(systemConfigHistoryInfo.getFileId(), SystemConfigRoleEnum.DELETE);
        }
        if (systemConfigHistoryDao.deleteById(id) != 1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }



    public CheckForUpdateVo checkForUpdate(DataConfigReq req) {

        String key =  systemConfigCacheService.getKey(req.getEnvCode(),req.getAppId());
        if (!systemConfigCacheService.containsKey(key)){
            return CheckForUpdateVo.builder()
                    .code(MarsConst.SUCCESS)
                    .version(-1L)
                    .build();
        }
        Long version = systemReleaseDao.getTopReleaseId(req.getEnvCode(),req.getAppId(),1);
        if (version==null){
            version = 0L;
        }
        systemConfigCacheService.setCache(key,version);

        return CheckForUpdateVo.builder()
                .code(MarsConst.SUCCESS)
                .version(version)
                .build();

        /*if (dataConfig != null && StringUtils.isEmpty(dataConfig.getToken())) {
            return null;
        }*/
        /*MD5 md5 = MD5.getInstance();
        String token =md5.getMD5String(getToken(dataConfig.getEnvCode(),dataConfig.getAppId(),dataConfig.getVersion()));
        if (!token.equals(dataConfig.getToken())){
            return null;
        }*/
        //return systemConfigDao.checkForUpdate(req);
    }


    public ForDataVo forDataVo(DataConfigReq dataConfig) {
        /*if (dataConfig != null && StringUtils.isEmpty(dataConfig.getToken())) {
            return null;
        }*/
        /*MD5 md5 = MD5.getInstance();
        String token =md5.getMD5String(getToken(dataConfig.getEnvCode(),dataConfig.getAppId(),dataConfig.getFileName(),dataConfig.getVersion()));
        if (!token.equals(dataConfig.getToken())){
            return null;
        }*/

        return null;
    }
}
