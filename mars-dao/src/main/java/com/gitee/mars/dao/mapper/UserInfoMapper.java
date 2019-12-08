package com.gitee.mars.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.mars.dao.entity.RoleInfo;
import com.gitee.mars.dao.entity.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo>{


    @Update("UPDATE user_info SET last_login_time=#{lastLoginTime} WHERE id=#{id}")
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") Date timestamp);

    @Select("SELECT a.id,a.user_name,a.real_name,a.`password`,a.create_date,a.`status`,a.last_login_time,a.update_date,c.role_name,c.id as roleId " +
            " from user_info a " +
            " left JOIN user_role_relation b on a.id=b.user_id" +
            " left JOIN role_info c on c.id=b.role_id")
    List<UserInfo> queryAll();

    @Select("select r.role_name " +
            "from user_info u " +
            "inner join user_role_relation ur on ur.user_id = u.id " +
            "INNER JOIN role_info r on r.id= ur.role_id  where u.id = #{userId}")
    RoleInfo findByUserId(@Param("userId") Long userId);
}
