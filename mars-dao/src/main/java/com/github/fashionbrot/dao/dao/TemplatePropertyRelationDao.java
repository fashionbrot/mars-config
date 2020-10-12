package com.github.fashionbrot.dao.dao;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fashionbrot.dao.entity.TemplatePropertyRelationEntity;


/**
 * 模板属性关系表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */

public interface TemplatePropertyRelationDao extends IService<TemplatePropertyRelationEntity> {


    int coountTemplateKeyAndAppName(String templateKey, String appName);
}