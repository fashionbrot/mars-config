package com.github.fashionbrot.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.fashionbrot.common.constant.MarsConst;
import com.github.fashionbrot.common.enums.OperationTypeEnum;
import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.enums.SystemConfigRoleEnum;
import com.github.fashionbrot.common.enums.SystemStatusEnum;
import com.github.fashionbrot.common.exception.CurdException;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.common.model.LoginModel;
import com.github.fashionbrot.common.req.ConfigValueApiReq;
import com.github.fashionbrot.common.req.DataConfigReq;
import com.github.fashionbrot.common.util.*;
import com.github.fashionbrot.common.vo.CheckForUpdateVo;
import com.github.fashionbrot.common.vo.ForDataVo;
import com.github.fashionbrot.common.vo.ForDataVoList;
import com.github.fashionbrot.common.vo.PageDataVo;
import com.github.fashionbrot.core.UserLoginService;
import com.github.fashionbrot.dao.dao.SystemConfigDao;
import com.github.fashionbrot.dao.dao.SystemConfigHistoryDao;
import com.github.fashionbrot.dao.dao.SystemConfigRoleRelationDao;
import com.github.fashionbrot.dao.dao.SystemReleaseDao;
import com.github.fashionbrot.dao.dto.ConfigValueDto;
import com.github.fashionbrot.dao.dto.SystemConfigDto;
import com.github.fashionbrot.dao.entity.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private Environment environment;

    private static final String CLUSTER="mars.cluster.address";
    private static final String SYNC_RETRY = "mars.cluster.sync.retry";
    private static final String SYSTEM_CONFIG_DEL="[del]";


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
    public void add(SystemConfigInfo info) {
        QueryWrapper<SystemConfigInfo> q=new QueryWrapper();
        q.eq("file_name",info.getFileName());
        if(systemConfigDao.count(q)>0){
            throw new MarsException(info.getFileName()+" 文件名称重复,请重新输入");
        }
        try {
            if ("yaml".equalsIgnoreCase(info.getFileType())) {
                //TODO 需要验证 数据格式
            }
        } catch (Exception e) {
            log.error(" add error", e);
            throw new MarsException("填写格式错误,请检查输入格式（ymal填写时不能有 TAB 换行）");
        }

        if (isFileHasExisted(info)) {
            throw new MarsException("配置文件已存在");
        }
        info.setTempJson(info.getJson());
        info.setJson("");
        info.setStatus(SystemStatusEnum.ADD.getCode());
        LoginModel login = userLoginService.getLogin();
        if (login != null) {
            info.setModifier(login.getUserName());
        }
        systemConfigDao.save(info);
        updateRelease(info.getEnvCode(),info.getAppName(),info.getFileName());
    }


    @Transactional(rollbackFor = Exception.class)
    public void update(SystemConfigInfo info) {

        QueryWrapper<SystemConfigInfo> q=new QueryWrapper();
        q.eq("file_name",info.getFileName());
        SystemConfigInfo temp = systemConfigDao.getOne(q);
        if(temp!=null && temp.getId().intValue()!=info.getId().intValue()){
            throw new MarsException(info.getFileName()+" 文件名称重复,请重新输入");
        }

        systemConfigRoleRelationDao.checkRole(info.getId(), SystemConfigRoleEnum.EDIT);

        SystemConfigInfo old = systemConfigDao.getById(info.getId());
        if (old == null) {
            throw new MarsException(RespCode.EXIST_SYSTEM_CONFIG_ERROR);
        }
        int oldStatus = old.getStatus().intValue();

        try {
            if ("yaml".equalsIgnoreCase(info.getFileType())) {
                Yaml yaml=new Yaml();
                yaml.load(info.getJson());
            }
        } catch (Exception e) {
            log.error(" update parse content error", e);
            throw new MarsException("填写格式错误,请检查输入格式（ymal填写时不能有 TAB 换行）");
        }
        String oldJson = StringUtil.isEmpty(old.getJson())?"":old.getJson();
        int flag = oldJson.compareTo(info.getJson());
        if (flag != 0 || !old.getFileType().equals(info.getFileType())) {
            //如果不是发布状态下修改，则状态还是保持
            if (oldStatus == SystemStatusEnum.RELEASE.getCode()) {
                old.setStatus(SystemStatusEnum.UPDATE.getCode());
            }
            old.setTempJson(info.getJson());
        }
        LoginModel userInfo = userLoginService.getLogin();
        if (userInfo != null) {
            old.setModifier(userInfo.getUserName());
        }
        old.setFileDesc(info.getFileDesc());
        old.setFileType(info.getFileType());
        QueryWrapper updateWrapper = new QueryWrapper<SystemConfigInfo>();
        updateWrapper.eq("id", old.getId());

        boolean result = systemConfigDao.update(old, updateWrapper);
        if (!result) {
            throw new MarsException(RespCode.UPDATE_REFRESH_ERROR);
        }
        //如果内容修改了，并且 是发布状态
        //增加一条修改记录，增加 system_release 记录
        if (flag != 0 && oldStatus==SystemStatusEnum.RELEASE.getCode()) {
            insertSystemConfigLog(old,SystemStatusEnum.UPDATE);
            updateRelease(old.getEnvCode(),old.getAppName(),old.getFileName());
        }
    }

    private void insertSystemConfigLog(SystemConfigInfo config,SystemStatusEnum statusEnum) {
        SystemConfigHistoryInfo systemConfigHistoryInfo = new SystemConfigHistoryInfo();
        BeanUtils.copyProperties(config, systemConfigHistoryInfo, "id");
        systemConfigHistoryInfo.setFileId(config.getId());
        if (statusEnum == SystemStatusEnum.UPDATE) {
            systemConfigHistoryInfo.setPreJson(config.getTempJson());
        }else if (statusEnum == SystemStatusEnum.DELETE){
            systemConfigHistoryInfo.setPreJson(config.getJson());
        }
        systemConfigHistoryInfo.setOperationType(statusEnum.getCode());
        systemConfigHistoryDao.insert(systemConfigHistoryInfo);
    }


    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {

        systemConfigRoleRelationDao.checkRole(id, SystemConfigRoleEnum.DELETE);

        SystemConfigInfo configInfo = systemConfigDao.getById(id);
        if (configInfo==null){
            throw new MarsException("配置不存在");
        }
        //如果是新增的直接删除
        if (configInfo.getStatus().intValue() == SystemStatusEnum.ADD.getCode()){
            systemConfigRoleRelationDao.delete(new QueryWrapper<SystemConfigRoleRelation>().eq("system_config_id", id));
            systemConfigDao.removeById(id);
            return;
        }if (configInfo.getStatus().intValue() == SystemStatusEnum.UPDATE.getCode()){
            throw new MarsException("配置已修改不能删除");
        }

        String preJson = configInfo.getJson();
        LoginModel login = userLoginService.getLogin();
        if (login!=null){
            configInfo.setModifier(login.getUserName());
        }


        configInfo.setStatus(SystemStatusEnum.DELETE.getCode());
        if (!systemConfigDao.updateById(configInfo)){
            throw new MarsException(RespCode.DELETE_ERROR);
        }
        insertSystemConfigLog(configInfo,SystemStatusEnum.DELETE);

        updateRelease(configInfo.getEnvCode(),configInfo.getAppName(),configInfo.getFileName()+SYSTEM_CONFIG_DEL);
    }

    public void unDel(Long id) {
        SystemConfigInfo configInfo = systemConfigDao.getById(id);
        if (configInfo==null){
            throw new MarsException("配置不存在");
        }
        configInfo.setStatus(SystemStatusEnum.RELEASE.getCode());
        if (!systemConfigDao.updateById(configInfo)){
            throw new MarsException("撤销失败");
        }
    }

    private ExecutorService executorService = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(5),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    if (!executor.isShutdown()) {
                        //再尝试入队
                        executor.execute(r);
                    }
                }
            });

    public void releaseConfig(SystemConfigInfo req) {
        QueryWrapper q = new QueryWrapper<ConfigReleaseEntity>()
                .eq("env_code",req.getEnvCode())
                .eq("app_name",req.getAppName())
                .eq("release_flag",0);
        SystemReleaseEntity releaseEntity = systemReleaseDao.getOne(q);
        if (releaseEntity==null){
            throw new MarsException("没有要发布的应用");
        }
        systemConfigDao.updateRelease(SystemConfigDto.builder()
                .envCode(req.getEnvCode())
                .appName(req.getAppName())
                .updateReleaseStatus(4)
                .whereReleaseStatus(Arrays.asList(1,2))
                .build());
        deleteByReleaseStatus(req.getEnvCode(),req.getAppName(),3);


        SystemReleaseEntity updateRelease=SystemReleaseEntity.builder()
                .releaseFlag(1)
                .build();
        if (!systemReleaseDao.update(updateRelease,q)){
            throw new MarsException(RespCode.FAIL);
        }

        String key = getKey(req.getEnvCode(),req.getAppName());
        systemConfigCacheService.setCache(key,releaseEntity.getId());

        if (environment.containsProperty(CLUSTER)){
            String cluster = environment.getProperty(CLUSTER);
            if (StringUtils.isEmpty(cluster)){
                return;
            }
            final int retry ;
            if (environment.containsProperty(SYNC_RETRY)){
                retry = StringUtil.parseInteger(environment.getProperty(SYNC_RETRY),3);
            }else{
                retry = 3;
            }

            List<String> serverList = ServerUtil.getServerList(cluster,"/api/config/cluster/sync");
            int count = serverList.size();
            if (count<=0){
                return;
            }


            List<String> params = new ArrayList<>();
            params.add("envCode");
            params.add(req.getEnvCode());
            params.add("appId");
            params.add(req.getAppName());
            params.add("version");
            params.add(releaseEntity.getId()+"");

            for (String s : serverList) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < retry; i++) {
                            HttpResult httpResult = HttpClientUtil.httpPost(s, null, params, "UTF-8", 2000, 2000);
                            if (httpResult.isSuccess() && (releaseEntity.getId().longValue() + "").equals(httpResult.getContent())) {
                                break;
                            }
                        }
                    }
                });
            }
        }

    }



    public Long clusterSync(ConfigValueApiReq req) {
        String key = getKey(req.getEnvCode(),req.getAppId());
        long version =req.getVersion().longValue();
        long v = 0;
        if (systemConfigCacheService.containsKey(key)){
            v = systemConfigCacheService.getCache(key).longValue();
        }
        if(v<version){
            systemConfigCacheService.setCache(key,req.getVersion());
        }
        return systemConfigCacheService.getCache(key);
    }

    private String getKey(String env,String app){
        return env+app;
    }

    private void deleteByReleaseStatus(String envCode,String appName,Integer whereReleaseStatus){
        QueryWrapper<SystemConfigInfo> qq=new QueryWrapper();
        qq.eq("env_code",envCode);
        qq.eq("app_name",appName);
        qq.eq("status",whereReleaseStatus);
        systemConfigDao.remove(qq);
    }


    public SystemConfigInfo queryById(Long id) {
        systemConfigRoleRelationDao.checkRole(id, SystemConfigRoleEnum.VIEW);

        SystemConfigInfo systemConfigInfo =  systemConfigDao.getById(id);
        if (systemConfigInfo!=null){
            if (systemConfigInfo.getStatus().intValue() == SystemStatusEnum.UPDATE.getCode()
                || systemConfigInfo.getStatus().intValue() == SystemStatusEnum.ADD.getCode() ) {
                systemConfigInfo.setJson(systemConfigInfo.getTempJson());
            }
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
        LoginModel userInfo = userLoginService.getLogin();

        SystemConfigInfo systemConfigInfo = systemConfigDao.getById(systemConfigHistoryInfo.getFileId());
        if (systemConfigInfo != null) {

            systemConfigRoleRelationDao.checkRole(systemConfigHistoryInfo.getFileId(), SystemConfigRoleEnum.EDIT);
            systemConfigInfo.setTempJson(systemConfigHistoryInfo.getJson());
            systemConfigInfo.setStatus(SystemStatusEnum.UPDATE.getCode());
            if (userInfo != null) {
                systemConfigInfo.setModifier(userInfo.getUserName());
            }
            systemConfigDao.updateById(systemConfigInfo);

        } else {
            SystemConfigInfo configInfo = SystemConfigInfo.builder()
                    .appName(systemConfigHistoryInfo.getAppName())
                    .fileDesc(systemConfigHistoryInfo.getFileName())
                    .envCode(systemConfigHistoryInfo.getEnvCode())
                    .fileType(systemConfigHistoryInfo.getFileType())
                    .fileName(systemConfigHistoryInfo.getFileName())
                    .status(SystemStatusEnum.ADD.getCode())
                    .tempJson(systemConfigHistoryInfo.getPreJson())
                    .build();
            if (userInfo != null) {
                configInfo.setModifier(userInfo.getUserName());
            }
            systemConfigDao.save(configInfo);
        }

        updateRelease(systemConfigHistoryInfo.getEnvCode(),systemConfigHistoryInfo.getAppName(),systemConfigHistoryInfo.getFileName());
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
        queryWrapper.select("id,file_id,file_name,modifier,app_name,env_code,operation_type,create_date");
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



    public long checkForUpdate(DataConfigReq req) {

        String key =  systemConfigCacheService.getKey(req.getEnvCode(),req.getAppId());
        if (systemConfigCacheService.containsKey(key)){
            return systemConfigCacheService.getCache(key);
        }

        Long version = systemReleaseDao.getTopReleaseId(req.getEnvCode(),req.getAppId(),1);
        if (version==null){
            version = 0L;
        }else {
            systemConfigCacheService.setCache(key, version);
        }

        return version;
    }


    public ForDataVoList forDataVo(DataConfigReq req) {
        String key =  systemConfigCacheService.getKey(req.getEnvCode(),req.getAppId());
        QueryWrapper<SystemConfigInfo> q=new QueryWrapper();
        q.select("file_name,file_type,json");
        q.eq("env_code",req.getEnvCode());
        q.eq("app_name",req.getAppId());
        SystemReleaseEntity release = null;
        List<String> delKeyList = null;
        List<String> keyList = null;
        //如果是客户端第一次调用,并且 本地缓存没有最新的version，则进行数据库查询
        if (req.getFirst()!=null && req.getFirst()){
            if (!systemConfigCacheService.containsKey(key)){
                Long version = systemReleaseDao.getTopReleaseId(req.getEnvCode(),req.getAppId(),1);
                if (version!=null){
                    systemConfigCacheService.setCache(key, version);
                }
            }

            List<SystemConfigInfo> list  = systemConfigDao.list(q);
            List<ForDataVo> forDataVoList = null;
            if (CollectionUtil.isNotEmpty(list)){
                forDataVoList = list.stream()
                        .filter(m-> StringUtil.isNotEmpty(m.getJson()))
                        .map(m-> changeForData(m))
                        .collect(Collectors.toList());
            }
            Long lastVersion = systemConfigCacheService.getCache(key);
            return ForDataVoList.builder()
                    .version(lastVersion)
                    .list(forDataVoList)
                    .build();
        }else{
            release = systemReleaseDao.getById(req.getVersion());
            if (release!=null){
                List<String> stringStream =  Arrays.stream(release.getFiles().split(",")).collect(Collectors.toList());
                keyList = stringStream.stream().filter(k-> !k.endsWith(SYSTEM_CONFIG_DEL)).collect(Collectors.toList());
                delKeyList = stringStream.stream().filter(k-> k.endsWith(SYSTEM_CONFIG_DEL)).map(k-> k.replace(SYSTEM_CONFIG_DEL,"")).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(keyList)){
                    q.in("file_name",keyList);
                }
            }

            List<SystemConfigInfo> list = null;
            if (CollectionUtil.isNotEmpty(keyList)) {
                list = systemConfigDao.list(q);
            }
            List<ForDataVo> forDataVoList = null;
            if (CollectionUtil.isNotEmpty(list)){
                forDataVoList = list.stream()
                        .filter(m-> StringUtil.isNotEmpty(m.getJson()))
                        .map(m-> changeForData(m))
                        .collect(Collectors.toList());

                addDelKey(delKeyList, forDataVoList);
            }else{
                forDataVoList = new ArrayList<>(delKeyList.size());
                addDelKey(delKeyList, forDataVoList);
            }

            Long lastVersion = systemConfigCacheService.getCache(key);
            return ForDataVoList.builder()
                    .version(lastVersion)
                    .list(forDataVoList)
                    .build();

        }
    }

    private void addDelKey(List<String> delKeyList, List<ForDataVo> forDataVoList) {
        if (CollectionUtil.isNotEmpty(delKeyList)) {
            for (int i = 0; i < delKeyList.size(); i++) {
                forDataVoList.add(ForDataVo.builder()
                        .fileName(delKeyList.get(i))
                        .fileType("properties")
                        .content(null)
                        .delFlag(true)
                        .build());
            }
        }
    }


    private ForDataVo changeForData(SystemConfigInfo info){
        return ForDataVo.builder()
                .fileName(info.getFileName())
                .fileType(info.getFileType())
                .content(info.getJson())
                .build();
    }

    public static void main(String[] args) {
        System.out.println(SYSTEM_CONFIG_DEL.replace(SYSTEM_CONFIG_DEL,""));
    }
}
