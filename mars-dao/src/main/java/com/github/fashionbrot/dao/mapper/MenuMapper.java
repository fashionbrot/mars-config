package com.github.fashionbrot.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fashionbrot.dao.entity.Menu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Repository
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> selectMenuRole(@Param("roleId") Long roleId);

    List<Menu> selectMenuRoleByUser(@Param("userId")Long userId);
}
