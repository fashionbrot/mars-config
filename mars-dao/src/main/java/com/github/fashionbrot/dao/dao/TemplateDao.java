package com.github.fashionbrot.dao.dao;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fashionbrot.dao.entity.TemplateEntity;


/**
 * 模板表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-12
 */

public interface TemplateDao extends IService<TemplateEntity> {


    int deleteByTemplateKeyAndAppName(String templateKey, String appName);
}