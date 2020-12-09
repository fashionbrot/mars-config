package com.github.fashionbrot.dao.dao;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fashionbrot.dao.entity.ConfigReleaseEntity;


/**
 * 配置数据发布表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-11-23
 */

public interface ConfigReleaseDao extends IService<ConfigReleaseEntity> {

    Long getTopReleaseId(String envCode,String appName,Integer releaseFlag);

}