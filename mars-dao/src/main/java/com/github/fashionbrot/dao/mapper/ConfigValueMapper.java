
package com.github.fashionbrot.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.common.vo.ConfigValueVo;
import com.github.fashionbrot.dao.dto.ConfigValueDto;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 配置数据表
 *
 * @author fashionbrot
 * @email fashionbrot@163.com
 * @date 2020-10-13
 */
@Mapper
public interface ConfigValueMapper extends BaseMapper<ConfigValueEntity> {

    @Update("update config_value set release_status=0  , del_flag=1 where id=#{id} ")
    int updateDelete(@Param("id")Long id);

    List<ConfigValueEntity> configValueList(ConfigValueReq req);

    List<ConfigValueVo> selectByJson(Map<String,Object> map);

    Integer updateRelease(ConfigValueDto dto);
}