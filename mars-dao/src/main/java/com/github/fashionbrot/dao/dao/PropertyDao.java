package com.github.fashionbrot.dao.dao;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fashionbrot.dao.entity.PropertyEntity;

import java.util.List;


/**
 * 属性表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */

public interface PropertyDao extends IService<PropertyEntity> {


    List<PropertyEntity> getPropertyList(String appName, String templateKey);

}