package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@TableName("system_config_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigHistoryInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value="create_date",fill = FieldFill.INSERT)
    private Date createDate;

    @TableField(value = "file_id")
    private Long fileId;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "env_code")
    private String envCode;

    @TableField(value = "app_name")
    private String appName;
    /**
     * 文件类型 TEXT YAML  Properties
     */
    @TableField(value = "file_type")
    private String fileType;

    @TableField(value = "modifier")
    private String modifier;

    @TableField(value = "pre_json")
    private String preJson;

    @TableField("json")
    private String json;

    @TableField("operation_type")
    private int operationType;
}
