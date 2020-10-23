package com.github.fashionbrot.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fashionbrot.dao.entity.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public interface TableColumnMapper extends BaseMapper<TableColumnEntity> {

    void addTable(@Param("tableName")String tableName);

    int dropTable(@Param("tableName")String tableName);

    void addTableColumn(PropertyEntity entity);

    void dropTableColumn(PropertyEntity entity);

    void updateTableColumn(PropertyEntity entity);

    int insertTable(@Param("sql") String sql);

    int updateTable(@Param("sql") String sql);

    Map<String, Object> selectTable(ConfigValueEntity configValue);
}
