
package com.github.fashionbrot.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fashionbrot.dao.entity.SystemReleaseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface SystemReleaseMapper extends BaseMapper<SystemReleaseEntity> {


    @Select("select id from system_release where env_code=#{envCode} and app_name=#{appName}  and release_flag=#{releaseFlag}  ORDER BY id desc  LIMIT 1")
    Long getTopReleaseId(@Param("envCode") String envCode,@Param("appName") String appName,@Param("releaseFlag")Integer releaseFlag);
}