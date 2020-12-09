
package com.github.fashionbrot.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fashionbrot.dao.entity.ConfigReleaseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 配置数据发布表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-11-23
 */
@Mapper
public interface ConfigReleaseMapper extends BaseMapper<ConfigReleaseEntity> {


    @Select("select id from config_release where env_code=#{envCode} and app_name=#{appName}  and release_flag=#{releaseFlag}  ORDER BY id desc  LIMIT 1")
    Long getTopReleaseId(@Param("envCode") String envCode,@Param("appName") String appName,@Param("releaseFlag")Integer releaseFlag);
}