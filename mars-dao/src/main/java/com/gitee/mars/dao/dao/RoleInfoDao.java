package com.gitee.mars.dao.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.mars.dao.entity.RoleInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleInfoDao extends BaseMapper<RoleInfo> {

    @Select("SELECT a.id,a.role_name,a.role_code from role_info a  " +
            "INNER JOIN user_role_relation b on a.id=b.role_id " +
            "INNER JOIN user_info c on c.id=b.user_id where c.user_name=#{username}")
    RoleInfo findByUserName(@Param("username") String userName);

}
