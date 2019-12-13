package com.github.fashionbrot.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fashionbrot.dao.entity.SystemConfigRoleRelation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Repository
public interface SystemConfigRoleRelationMapper  extends BaseMapper<SystemConfigRoleRelation> {

    @Select("SELECT b.id,b.system_config_id,a.file_name,b.view_status,b.edit_status,b.delete_status,b.push_status from system_config_info a " +
            "INNER JOIN system_config_role_relation b on a.id = b.system_config_id " +
            "where a.env_code =#{envCode} and a.app_name=#{appName}  and b.role_id = #{roleId} ")
    List<SystemConfigRoleRelation> selectBy(@Param("envCode")String envCode, @Param("appName")String appName, @Param("roleId")Long roleId);

    @Select("SELECT b.id,b.view_status,b.edit_status,b.delete_status,b.push_status from user_role_relation a " +
            "INNER JOIN system_config_role_relation b on a.role_id = b.role_id " +
            "where b.system_config_id =#{systemConfigId} and a.user_id =#{userId}")
    SystemConfigRoleRelation selectByRole(@Param("systemConfigId")Long systemConfigId,@Param("userId") Long userId);


}
