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
@TableName("system_config_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value="create_date",fill = FieldFill.INSERT)
    private Date createDate;

    @TableField(value = "update_date",fill = FieldFill.UPDATE)
    private Date updateDate;

    @TableField(value = "env_code")
    private String envCode;

    @TableField(value = "app_name")
    private String appName;

    @TableField(value = "modifier")
    private String modifier;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "file_desc")
    private String fileDesc;

    @TableField("json")
    private String json;

    @TableField("temp_json")
    private String tempJson;
    /**
     * 文件类型 TEXT YAML  Properties
     */
    @TableField(value = "file_type")
    private String fileType;

    @TableField(value = "status")
    private Integer status;

    private transient Long roleId;

    private transient Long nowUpdateDate;
}
