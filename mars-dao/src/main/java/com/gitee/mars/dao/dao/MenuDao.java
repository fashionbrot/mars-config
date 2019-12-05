package com.gitee.mars.dao.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.mars.dao.dao.provider.MenuRoleRelationProvider;
import com.gitee.mars.dao.entity.Menu;
import com.gitee.mars.dao.entity.MenuRoleRelation;
import com.gitee.mars.dao.entity.UserInfo;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuDao extends BaseMapper<Menu> {

    @SelectProvider(method = "selectMenuRole",type = MenuRoleRelationProvider.class)
    List<Menu> selectMenuRole(MenuRoleRelation relation);


    @SelectProvider(method = "selectMenuRoleByUser",type = MenuRoleRelationProvider.class)
    List<Menu> selectMenuRoleByUser(UserInfo userInfo);
}
