package com.github.fashionbrot.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.fashionbrot.dao.entity.MenuRoleRelation;
import com.github.fashionbrot.dao.mapper.MenuRoleRelationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Service
@Slf4j
public class MenuRoleRelationDao  {

    @Autowired
    private MenuRoleRelationMapper menuRoleRelationMapper;


    public int delete(QueryWrapper queryWrapper) {
        return menuRoleRelationMapper.delete(queryWrapper);
    }


    public int insert(MenuRoleRelation relation) {
        relation.setUpdateDate(new Date());
        return menuRoleRelationMapper.insert(relation);
    }
}
