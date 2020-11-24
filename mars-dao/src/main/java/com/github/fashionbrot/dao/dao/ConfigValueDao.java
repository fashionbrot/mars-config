package com.github.fashionbrot.dao.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fashionbrot.common.enums.DateEnum;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.common.util.DateUtil;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import com.github.fashionbrot.dao.entity.PropertyEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    int updateDelete(Long id);

    List<Map<String,Object>> configValueList(ConfigValueReq req);

    ConfigValueEntity queryById(Long id);

    void formatDate(List<PropertyEntity> list, Map<String, Object> map);

    void formatDate(List<PropertyEntity> list, Map.Entry<String, Object> map, String column);
}