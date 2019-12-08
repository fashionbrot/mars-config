package com.gitee.mars.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.mars.dao.entity.Menu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> selectMenuRole(@Param("roleId") Long roleId);

    List<Menu> selectMenuRoleByUser(@Param("userId")Long userId);
}
