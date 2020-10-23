package com.github.fashionbrot.dao.dao;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;

import java.util.List;
import java.util.Map;


/**
 * 配置数据表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */

public interface ConfigValueDao extends IService<ConfigValueEntity> {


    List<Map<String,Object>> configValueList(ConfigValueReq req);
}