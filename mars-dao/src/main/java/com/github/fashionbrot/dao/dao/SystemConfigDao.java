package com.github.fashionbrot.dao.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fashionbrot.dao.dto.SystemConfigDto;
import com.github.fashionbrot.dao.entity.SystemConfigInfo;

public interface SystemConfigDao extends IService<SystemConfigInfo> {


    int updateRelease(SystemConfigDto build);

}
