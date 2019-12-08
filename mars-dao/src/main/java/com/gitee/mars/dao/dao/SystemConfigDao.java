package com.gitee.mars.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.common.enums.ApiResultEnum;
import com.gitee.mars.common.enums.OperationTypeEnum;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.enums.SystemConfigRoleEnum;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.common.req.DataConfigReq;
import com.gitee.mars.common.util.FileUtil;
import com.gitee.mars.common.util.SnowflakeIdWorkerUtil;
import com.gitee.mars.common.util.SystemUtil;
import com.gitee.mars.common.vo.CheckForUpdateVo;
import com.gitee.mars.common.vo.ForDataVo;
import com.gitee.mars.dao.entity.*;
import com.gitee.mars.dao.mapper.SystemConfigMapper;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SystemConfigDao {
    @Autowired
    private SystemConfigMapper systemConfigDao;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private SystemConfigHistoryDao systemConfigHistoryDao;

    @Autowired
    private AppInfoDao appInfoDao;


    @Autowired
    private SystemConfigRoleRelationDao systemConfigRoleRelationDao;

    private SnowflakeIdWorkerUtil snowflakeIdWorkerUtil = new SnowflakeIdWorkerUtil(SystemUtil.IP_LAST_POINT,1);

    @Autowired
    private SystemConfigRoleRelationDao systemConfigRoleRelationService;


    @Transactional(rollbackFor = Exception.class)
    public int add(SystemConfigInfo systemConfigInfo) {

        /*if (!FileUtil.isMatch(systemConfigInfo.getFileName())){
            throw new MarsException("文件名不能以 "+systemConfigInfo.getFileName() +"为后缀");
        }*/

        if(isFileHasExisted(systemConfigInfo)){
            throw new MarsException(RespCode.EXIST_ERROR,"配置文件");
        }


        UserInfo userInfo = userInfoDao.getUser();
        if (userInfo!=null) {
            systemConfigInfo.setCreator(userInfo.getRealName());
            systemConfigInfo.setModifier(userInfo.getRealName());
        }

        systemConfigInfo.setVersion(snowflakeIdWorkerUtil.nextId()+"");
        systemConfigInfo.setFileName(systemConfigInfo.getFileName());

        systemConfigInfo.setCreateDate(new Date());
        return systemConfigDao.insert(systemConfigInfo);
    }


    @Transactional(rollbackFor = Exception.class)
    public int update(SystemConfigInfo systemConfigInfo) {

         return updateConfig(systemConfigInfo,null, OperationTypeEnum.UPDATE);
    }

    public int updateConfig(SystemConfigInfo systemConfigInfo,String json,OperationTypeEnum operationTypeEnum){

            systemConfigRoleRelationService.checkRole(systemConfigInfo.getId(), SystemConfigRoleEnum.EDIT);

            SystemConfigInfo systemConfigInfoPre = systemConfigDao.selectById(systemConfigInfo.getId());
            //TODO 需要验证
            /*try {
                if ("yaml".equalsIgnoreCase(systemConfigInfo.getFileType())) {
                    ConfigParseUtils.toProperties(systemConfigInfo.getJson(), systemConfigInfoPre.getFileType());
                }
            }catch (Exception e){
                log.error(" update parse content error",e);
                throw new MarsException("填写格式错误,请检查输入格式（ymal填写时不能有 TAB 换行）");
            }*/
            int flag =0;
            String preFileJson = systemConfigInfoPre.getJson();
            if (operationTypeEnum == OperationTypeEnum.ROLLBACK){
                flag = systemConfigInfoPre.getJson().compareTo(json);
                preFileJson = json;
            }else{
                flag = systemConfigInfoPre.getJson().compareTo(systemConfigInfo.getJson());
            }

            if (flag!=0){
                systemConfigInfoPre.setStatus(Byte.parseByte("0"));
            }



            UserInfo userInfo = userInfoDao.getUser();
            if (userInfo!=null) {
                systemConfigInfoPre.setModifier(userInfo.getRealName());
            }




            systemConfigInfoPre.setUpdateDate(new Date());
            systemConfigInfoPre.setFileDesc(systemConfigInfo.getFileDesc());
            systemConfigInfoPre.setJson(systemConfigInfo.getJson());

            int result = systemConfigDao.updateById(systemConfigInfoPre);

            if (flag!=0){
                systemConfigHistoryDao.insert(generateHistoryInfo(systemConfigInfoPre, preFileJson,operationTypeEnum));
            }
            return result;
    }


    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Long id) {
        systemConfigRoleRelationService.checkRole(id, SystemConfigRoleEnum.DELETE);

        SystemConfigInfo systemConfigInfo = systemConfigDao.selectById(id);
        String preJson = systemConfigInfo.getJson();
        UserInfo userInfo = userInfoDao.getUser();
        systemConfigInfo.setModifier(userInfo.getRealName());
        systemConfigInfo.setUpdateDate(new Date());
        systemConfigInfo.setJson("");

        systemConfigHistoryDao.insert(generateHistoryInfo(systemConfigInfo, preJson,OperationTypeEnum.DELETE));

        systemConfigRoleRelationDao.delete(new QueryWrapper<SystemConfigRoleRelation>().eq("system_config_id",id));
        return systemConfigDao.deleteById(id);

    }


    public SystemConfigInfo queryById(Long id) {
        systemConfigRoleRelationService.checkRole(id, SystemConfigRoleEnum.VIEW);
        return systemConfigDao.selectById(id);
    }


    public List<SystemConfigInfo> queryAll(QueryWrapper queryWrapper) {
        return systemConfigDao.selectList(queryWrapper);
    }


    public List<SystemConfigInfo> queryByAppAndEnv(String appName, String envCode) {
        QueryWrapper queryWrapper = new QueryWrapper<SystemConfigInfo>().
                eq("app_name", appName).
                eq("env_code", envCode);
        queryWrapper.select("id,file_type,env_code,app_name,file_name,file_desc,modifier,update_date,status");
        queryWrapper.orderByDesc("id");
        return systemConfigDao.selectList(queryWrapper);
    }


    public List<SystemConfigHistoryInfo> queryHistoryByFileId(Long fileId) {
        return systemConfigHistoryDao.selectList(new QueryWrapper<SystemConfigHistoryInfo>().eq(("file_id"), fileId));
    }


    @Transactional(rollbackFor = Exception.class)
    public int rollBackById(Long id) {

        SystemConfigHistoryInfo systemConfigHistoryInfo = systemConfigHistoryDao.selectById(id);
        if(systemConfigHistoryInfo == null){
            throw new MarsException(RespCode.HISTORY_CONFIG_NOT_EXIST);
        }
        SystemConfigInfo systemConfigInfo = systemConfigDao.selectById(systemConfigHistoryInfo.getFileId());
        if (systemConfigInfo!=null){

            systemConfigRoleRelationService.checkRole(systemConfigHistoryInfo.getFileId(), SystemConfigRoleEnum.EDIT);
            systemConfigInfo.setJson(systemConfigHistoryInfo.getPreJson());
            return updateConfig(systemConfigInfo,systemConfigHistoryInfo.getJson(),OperationTypeEnum.ROLLBACK);

        }else{
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String[] file= systemConfigHistoryInfo.getFileName().split("\\.");
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
        systemConfigRoleRelationService.checkRole(id, SystemConfigRoleEnum.PUSH);
        SystemConfigInfo systemConfigInfo = systemConfigDao.selectById(id);
        if (systemConfigInfo!=null && systemConfigInfo.getStatus() ==1){
            return;
        }
        systemConfigInfo.setStatus(1);


        AppInfo appInfo =appInfoDao.selectOne(new QueryWrapper<AppInfo>().eq("app_name",systemConfigInfo.getAppName()));
        if (appInfo==null) {
            throw new MarsException(RespCode.ENV_NOT_EXIST);
        }
        systemConfigInfo.setVersion(snowflakeIdWorkerUtil.nextId()+"");

        int  result = systemConfigDao.updateById(systemConfigInfo);
        if (result!=1){
            throw new MarsException(RespCode.PUBLISH_ERROR);
        }
    }


    public CheckForUpdateVo checkForUpdate(DataConfigReq dataConfig) {
        QueryWrapper queryWrapper= new QueryWrapper<SystemConfigInfo>().

                eq("env_code",dataConfig.getEnvCode()).
                eq("app_name",dataConfig.getAppId());
        queryWrapper.select("file_name,version,status");
        if (StringUtils.isNotEmpty(dataConfig.getVersion())){
            List<String> versionList =Arrays.stream(dataConfig.getVersion().split(",")).map( v -> {
                return v;
            }).collect(Collectors.toList());
            queryWrapper.notIn("version",versionList);

            List<SystemConfigInfo> systemConfigInfos =systemConfigDao.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(systemConfigInfos)){
                List<String> fileNames = systemConfigInfos.stream()
                        .filter(info ->{
                            if (info.getStatus()==1){
                                return true;
                            }else{
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
            }else{
                return null;
            }
        }else{
            List<SystemConfigInfo> systemConfigInfos =systemConfigDao.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(systemConfigInfos)){
                List<String> fileNames = systemConfigInfos.stream().map(config -> {
                    return config.getFileName();
                }).collect(Collectors.toList());
                return CheckForUpdateVo.builder()
                        .resultCode(ApiResultEnum.SUCCESS_UPDATE.getResultCode())
                        .updateFiles(fileNames)
                        .build();
            }else{
                return null;
            }
        }

    }


    public ForDataVo forDataVo(DataConfigReq dataConfig) {
        if (StringUtils.isEmpty(dataConfig.getEnvCode()) || StringUtils.isEmpty(dataConfig.getAppId()) || StringUtils.isEmpty(dataConfig.getFileName())){
            log.info("forDataVo error dataConfig:{}",dataConfig);
            return null;
        }
        QueryWrapper queryWrapper= new QueryWrapper<SystemConfigInfo>().
                eq("env_code",dataConfig.getEnvCode()).
                eq("app_name",dataConfig.getAppId()).
                eq("file_name",dataConfig.getFileName());
        queryWrapper.select("file_name,json,version");

        SystemConfigInfo configInfo =systemConfigDao.selectOne(queryWrapper);
        if (configInfo!=null){
            return ForDataVo.builder()
                    .fileName(configInfo.getFileName())
                    .content(configInfo.getJson())
                    .version(configInfo.getVersion())
                    .build();
        }
        return null;
    }

    private boolean isFileHasExisted(SystemConfigInfo systemConfigInfo){
        Integer count = systemConfigDao.selectCount(new QueryWrapper<SystemConfigInfo>().
                eq("env_code",systemConfigInfo.getEnvCode()).
                eq("app_name",systemConfigInfo.getAppName()).
                eq("file_name", systemConfigInfo.getFileName()+"."+systemConfigInfo.getFileType()));
        if(count>0){
            return true;
        }
        return false;
    }

    private SystemConfigHistoryInfo generateHistoryInfo(SystemConfigInfo systemConfigInfo, String preFileJson, OperationTypeEnum operationTypeEnum){
        SystemConfigHistoryInfo systemConfigHistoryInfo = new SystemConfigHistoryInfo();
        BeanUtils.copyProperties(systemConfigInfo, systemConfigHistoryInfo, "id");
        systemConfigHistoryInfo.setFileId(systemConfigInfo.getId());
        systemConfigHistoryInfo.setPreJson(preFileJson);
        systemConfigHistoryInfo.setOperationType( operationTypeEnum.getCode());
        return systemConfigHistoryInfo;
    }
}
