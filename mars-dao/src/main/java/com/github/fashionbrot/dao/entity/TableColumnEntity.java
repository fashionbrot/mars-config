package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("information_schema.columns")
public class TableColumnEntity {

    @TableField("table_name")
    private String tableName;

    @TableField("column_name")
    private String columnName;

    @TableField("data_type")
    private String dataType;

    @TableField("column_type")
    private String columnType;

    @TableField("column_comment")
    private String columnComment;

    @TableField("column_key")
    private String columnKey;

    @TableField("extra")
    private String extra;

    @TableField("ordinal_position")
    private Integer ordinalPosition;
}
