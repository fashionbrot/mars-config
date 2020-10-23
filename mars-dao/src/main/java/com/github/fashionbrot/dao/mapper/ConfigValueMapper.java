
package com.github.fashionbrot.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fashionbrot.common.req.ConfigValueReq;
import com.github.fashionbrot.dao.entity.ConfigValueEntity;
import org.apache.ibatis.annotations.Mapper;

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


    List<Map<String,Object>> configValueList(ConfigValueReq req);
}