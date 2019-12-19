package com.github.fashionbrot.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.common.enums.ApiResultEnum;
import com.github.fashionbrot.common.enums.OperationTypeEnum;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.enums.SystemConfigRoleEnum;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.req.DataConfigReq;
import com.github.fashionbrot.common.util.SnowflakeIdWorkerUtil;
import com.github.fashionbrot.common.util.SystemUtil;
import com.github.fashionbrot.common.vo.CheckForUpdateVo;
import com.github.fashionbrot.common.vo.ForDataVo;
import com.github.fashionbrot.dao.entity.*;
import com.github.fashionbrot.dao.mapper.SystemConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
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
public class SystemConfigDao {
    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private SystemConfigHistoryDao systemConfigHistoryDao;

    @Autowired
    private AppInfoDao appInfoDao;


    @Autowired
    private SystemConfigRoleRelationDao systemConfigRoleRelationDao;

    private SnowflakeIdWorkerUtil snowflakeIdWorkerUtil = new SnowflakeIdWorkerUtil(SystemUtil.IP_LAST_POINT, 1);


    @Transactional(rollbackFor = Exception.class)
    public int add(SystemConfigInfo systemConfigInfo) {

        /*if (!FileUtil.isMatch(systemConfigInfo.getFileName())){
            throw new MarsException("文件名不能以 "+systemConfigInfo.getFileName() +"为后缀");
        }*/

        if (isFileHasExisted(systemConfigInfo)) {
            throw new MarsException(RespCode.EXIST_ERROR, "配置文件");
        }


        UserInfo userInfo = userInfoDao.getUser();
        if (userInfo != null) {
            systemConfigInfo.setCreator(userInfo.getRealName());
            systemConfigInfo.setModifier(userInfo.getRealName());
        }

        systemConfigInfo.setVersion(snowflakeIdWorkerUtil.nextId() + "");
        systemConfigInfo.setFileName(systemConfigInfo.getFileName());

        systemConfigInfo.setCreateDate(new Date());
        return systemConfigMapper.insert(systemConfigInfo);
    }


    @Transactional(rollbackFor = Exception.class)
    public int update(SystemConfigInfo systemConfigInfo) {

        return updateConfig(systemConfigInfo, null, OperationTypeEnum.UPDATE);
    }

    public int updateConfig(SystemConfigInfo systemConfigInfo, String json, OperationTypeEnum operationTypeEnum) {

        systemConfigRoleRelationDao.checkRole(systemConfigInfo.getId(), SystemConfigRoleEnum.EDIT);

        SystemConfigInfo systemConfigInfoPre = systemConfigMapper.selectById(systemConfigInfo.getId());
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
        int flag = 0;
        String preFileJson = systemConfigInfoPre.getJson();
        if (operationTypeEnum == OperationTypeEnum.ROLLBACK) {
            flag = systemConfigInfoPre.getJson().compareTo(json);
            preFileJson = json;
        } else {
            flag = systemConfigInfoPre.getJson().compareTo(systemConfigInfo.getJson());
        }

        if (flag != 0) {
            systemConfigInfoPre.setStatus(Byte.parseByte("0"));
        }


        UserInfo userInfo = userInfoDao.getUser();
        if (userInfo != null) {
            systemConfigInfoPre.setModifier(userInfo.getRealName());
        }


        systemConfigInfoPre.setUpdateDate(new Date());
        systemConfigInfoPre.setFileDesc(systemConfigInfo.getFileDesc());
        systemConfigInfoPre.setJson(systemConfigInfo.getJson());
        systemConfigInfoPre.setFileType(systemConfigInfo.getFileType());

        QueryWrapper updateWrapper = new QueryWrapper<SystemConfigInfo>();
        updateWrapper.eq("id",systemConfigInfoPre.getId());
        if (systemConfigInfo.getNowUpdateDate()!=null){
            updateWrapper.eq("update_date",new Date(systemConfigInfo.getNowUpdateDate().longValue()));
        }

        int result = systemConfigMapper.update(systemConfigInfoPre,updateWrapper);
        //判断乐观锁
        if (result==0){
            Integer count = systemConfigMapper.selectCount(updateWrapper);
            if (count==null || count==0){
                throw new MarsException(RespCode.UPDATE_REFRESH_ERROR);
            }
        }
        if (flag != 0) {
            systemConfigHistoryDao.insert(generateHistoryInfo(systemConfigInfoPre, preFileJson, operationTypeEnum));
        }
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Long id) {
        systemConfigRoleRelationDao.checkRole(id, SystemConfigRoleEnum.DELETE);

        SystemConfigInfo systemConfigInfo = systemConfigMapper.selectById(id);
        String preJson = systemConfigInfo.getJson();
        UserInfo userInfo = userInfoDao.getUser();
        systemConfigInfo.setModifier(userInfo.getRealName());
        systemConfigInfo.setUpdateDate(new Date());
        systemConfigInfo.setJson("");

        systemConfigHistoryDao.insert(generateHistoryInfo(systemConfigInfo, preJson, OperationTypeEnum.DELETE));

        systemConfigRoleRelationDao.delete(new QueryWrapper<SystemConfigRoleRelation>().eq("system_config_id", id));
        return systemConfigMapper.deleteById(id);

    }


    public SystemConfigInfo queryById(Long id) {
        systemConfigRoleRelationDao.checkRole(id, SystemConfigRoleEnum.VIEW);
        return systemConfigMapper.selectById(id);
    }


    public List<SystemConfigInfo> queryAll(QueryWrapper queryWrapper) {
        return systemConfigMapper.selectList(queryWrapper);
    }


    public List<SystemConfigInfo> queryByAppAndEnv(String appName, String envCode) {
        QueryWrapper queryWrapper = new QueryWrapper<SystemConfigInfo>().
                eq("app_name", appName).
                eq("env_code", envCode);
        queryWrapper.select("id,file_type,env_code,app_name,file_name,file_desc,modifier,update_date,status");
        queryWrapper.orderByDesc("id");
        return systemConfigMapper.selectList(queryWrapper);
    }


    public List<SystemConfigHistoryInfo> queryHistoryByFileId(Long fileId) {
        return systemConfigHistoryDao.selectList(new QueryWrapper<SystemConfigHistoryInfo>().eq(("file_id"), fileId));
    }


    @Transactional(rollbackFor = Exception.class)
    public int rollBackById(Long id) {

        SystemConfigHistoryInfo systemConfigHistoryInfo = systemConfigHistoryDao.selectById(id);
        if (systemConfigHistoryInfo == null) {
            throw new MarsException(RespCode.HISTORY_CONFIG_NOT_EXIST);
        }
        SystemConfigInfo systemConfigInfo = systemConfigMapper.selectById(systemConfigHistoryInfo.getFileId());
        if (systemConfigInfo != null) {

            systemConfigRoleRelationDao.checkRole(systemConfigHistoryInfo.getFileId(), SystemConfigRoleEnum.EDIT);
            systemConfigInfo.setJson(systemConfigHistoryInfo.getPreJson());
            return updateConfig(systemConfigInfo, systemConfigHistoryInfo.getJson(), OperationTypeEnum.ROLLBACK);

        } else {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String[] file = systemConfigHistoryInfo.getFileName().split("\\.");
            SystemConfigInfo configInfo = SystemConfigInfo.builder()
                    .appName(systemConfigHistoryInfo.getAppName())
                    .envCode(systemConfigHistoryInfo.getEnvCode())
                    .createDate(timestamp)
                    .fileType(file[1])
                    .fileName(file[0])
                    .creator(systemConfigHistoryInfo.getModifier())
                    .status(Byte.valueOf("0"))
                    .json(systemConfigHistoryInfo.getPreJson())
                    .build();

            return add(configInfo);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        systemConfigRoleRelationDao.checkRole(id, SystemConfigRoleEnum.PUSH);
        SystemConfigInfo systemConfigInfo = systemConfigMapper.selectById(id);
        if (systemConfigInfo != null && systemConfigInfo.getStatus() == 1) {
            return;
        }
        systemConfigInfo.setStatus(1);


        AppInfo appInfo = appInfoDao.selectOne(new QueryWrapper<AppInfo>().eq("app_name", systemConfigInfo.getAppName()));
        if (appInfo == null) {
            throw new MarsException(RespCode.ENV_NOT_EXIST);
        }
        systemConfigInfo.setVersion(snowflakeIdWorkerUtil.nextId() + "");

        int result = systemConfigMapper.updateById(systemConfigInfo);
        if (result != 1) {
            throw new MarsException(RespCode.PUBLISH_ERROR);
        }
    }


    public CheckForUpdateVo checkForUpdate(DataConfigReq dataConfig) {
        QueryWrapper queryWrapper = new QueryWrapper<SystemConfigInfo>().
                eq("env_code", dataConfig.getEnvCode()).
                eq("app_name", dataConfig.getAppId());
        queryWrapper.select("file_name,version,status");
        if (StringUtils.isNotEmpty(dataConfig.getVersion())) {
            List<String> versionList = Arrays.stream(dataConfig.getVersion().split(",")).map(v -> {
                return v;
            }).collect(Collectors.toList());
            queryWrapper.notIn("version", versionList);

            List<SystemConfigInfo> systemConfigInfos = systemConfigMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(systemConfigInfos)) {
                List<String> fileNames = systemConfigInfos.stream()
                        .filter(info -> {
                            if (info.getStatus() == 1) {
                                return true;
                            } else {
                                return false;
                            }
                        })
                        .map(config -> {
                            return config.getFileName();
                        }).collect(Collectors.toList());

                return CheckForUpdateVo.builder()
                        .resultCode(ApiResultEnum.SUCCESS_UPDATE.getResultCode())
                        .updateFiles(fileNames)
                        .build();
            } else {
                return null;
            }
        } else {
            List<SystemConfigInfo> systemConfigInfos = systemConfigMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(systemConfigInfos)) {
                List<String> fileNames = systemConfigInfos.stream().map(config -> {
                    return config.getFileName();
                }).collect(Collectors.toList());
                return CheckForUpdateVo.builder()
                        .resultCode(ApiResultEnum.SUCCESS_UPDATE.getResultCode())
                        .updateFiles(fileNames)
                        .build();
            } else {
                return null;
            }
        }

    }


    public ForDataVo forDataVo(DataConfigReq dataConfig) {
        if (StringUtils.isEmpty(dataConfig.getEnvCode()) || StringUtils.isEmpty(dataConfig.getAppId()) || StringUtils.isEmpty(dataConfig.getFileName())) {
            log.info("forDataVo error dataConfig:{}", dataConfig);
            return null;
        }
        QueryWrapper queryWrapper = new QueryWrapper<SystemConfigInfo>().
                eq("env_code", dataConfig.getEnvCode()).
                eq("app_name", dataConfig.getAppId()).
                eq("file_name", dataConfig.getFileName());
        queryWrapper.select("file_name,json,version");

        SystemConfigInfo configInfo = systemConfigMapper.selectOne(queryWrapper);
        if (configInfo != null) {
            return ForDataVo.builder()
                    .fileName(configInfo.getFileName())
                    .content(configInfo.getJson())
                    .version(configInfo.getVersion())
                    .build();
        }
        return null;
    }

    private boolean isFileHasExisted(SystemConfigInfo systemConfigInfo) {
        Integer count = systemConfigMapper.selectCount(new QueryWrapper<SystemConfigInfo>().
                eq("env_code", systemConfigInfo.getEnvCode()).
                eq("app_name", systemConfigInfo.getAppName()).
                eq("file_name", systemConfigInfo.getFileName() + "." + systemConfigInfo.getFileType()));
        if (count > 0) {
            return true;
        }
        return false;
    }

    private SystemConfigHistoryInfo generateHistoryInfo(SystemConfigInfo systemConfigInfo, String preFileJson, OperationTypeEnum operationTypeEnum) {
        SystemConfigHistoryInfo systemConfigHistoryInfo = new SystemConfigHistoryInfo();
        BeanUtils.copyProperties(systemConfigInfo, systemConfigHistoryInfo, "id");
        systemConfigHistoryInfo.setFileId(systemConfigInfo.getId());
        systemConfigHistoryInfo.setPreJson(preFileJson);
        systemConfigHistoryInfo.setOperationType(operationTypeEnum.getCode());
        return systemConfigHistoryInfo;
    }
}
