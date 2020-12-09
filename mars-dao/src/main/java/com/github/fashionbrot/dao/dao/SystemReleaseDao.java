package com.github.fashionbrot.dao.dao;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fashionbrot.dao.entity.SystemReleaseEntity;




public interface SystemReleaseDao extends IService<SystemReleaseEntity> {

    Long getTopReleaseId(String envCode,String appName,Integer releaseFlag);

}