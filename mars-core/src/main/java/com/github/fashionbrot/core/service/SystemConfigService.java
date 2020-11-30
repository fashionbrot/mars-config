package com.github.fashionbrot.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.common.constant.MarsConst;
import com.github.fashionbrot.common.enums.ApiResultEnum;
import com.github.fashionbrot.common.enums.OperationTypeEnum;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.enums.SystemConfigRoleEnum;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.req.DataConfigReq;
import com.github.fashionbrot.common.vo.CheckForUpdateVo;
import com.github.fashionbrot.common.vo.ForDataVo;
import com.github.fashionbrot.common.vo.PageDataVo;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.UserLoginService;
import com.github.fashionbrot.dao.dao.SystemConfigDao;
import com.github.fashionbrot.dao.dao.SystemConfigHistoryDao;
import com.github.fashionbrot.dao.dao.SystemConfigRoleRelationDao;
import com.github.fashionbrot.dao.dao.SystemReleaseDao;
import com.github.fashionbrot.dao.entity.SystemConfigHistoryInfo;
import com.github.fashionbrot.dao.entity.SystemConfigInfo;
import com.github.fashionbrot.dao.entity.UserInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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


    public void add(SystemConfigInfo systemConfigInfo) {
        try {
            if ("yaml".equalsIgnoreCase(systemConfigInfo.getFileType())) {
                //TODO 需要验证 数据格式
            }
        } catch (Exception e) {
            log.error(" add error", e);
            throw new MarsException("填写格式错误,请检查输入格式（ymal填写时不能有 TAB 换行）");
        }
        if (systemConfigDao.add(systemConfigInfo) != 1) {
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }


    @Autowired
    private UserLoginService userLoginService;

    @Transactional(rollbackFor = Exception.class)
    public void update(SystemConfigInfo systemConfigInfo) {

        systemConfigRoleRelationDao.checkRole(systemConfigInfo.getId(), SystemConfigRoleEnum.EDIT);

        SystemConfigInfo systemConfigInfoPre = systemConfigDao.selectById(systemConfigInfo.getId());
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

        /*int result = systemConfigDao.update(systemConfigInfoPre,updateWrapper);
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
        return result;*/


        if (systemConfigDao.update(systemConfigInfo) != 1) {
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        if (systemConfigDao.deleteById(id) != 1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public SystemConfigInfo queryById(Long id) {
        SystemConfigInfo systemConfigInfo =  systemConfigDao.queryById(id);
        if (systemConfigInfo!=null){
            systemConfigInfo.setNowUpdateDate(systemConfigInfo.getUpdateDate()!=null?systemConfigInfo.getUpdateDate().getTime():null);
        }
        return systemConfigInfo;
    }


    public List<SystemConfigInfo> queryAll() {
        return systemConfigDao.queryAll(null);
    }


    public List<SystemConfigInfo> queryByAppAndEnv(String appName, String envCode) {
        return systemConfigDao.queryByAppAndEnv(appName, envCode);
    }


    public SystemConfigHistoryInfo queryHistoryById(Long fileId) {
        SystemConfigHistoryInfo systemConfigHistoryInfo = systemConfigHistoryDao.selectById(fileId);
        if (systemConfigHistoryInfo!=null) {
            systemConfigRoleRelationDao.checkRole(systemConfigHistoryInfo.getFileId(), SystemConfigRoleEnum.VIEW);
        }
        return systemConfigHistoryInfo;
    }


    public void rollBackById(Long id) {
        if (systemConfigDao.rollBackById(id) != 1) {
            throw new MarsException(RespCode.ROLL_BACK_ERROR);
        }
    }


    public RespCode publish(Long id) {
        systemConfigDao.publish(id);
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

    @Autowired
    private SystemConfigCacheService systemConfigCacheService;
    @Autowired
    private SystemReleaseDao systemReleaseDao;

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

        return systemConfigDao.forDataVo(dataConfig);
    }
}
