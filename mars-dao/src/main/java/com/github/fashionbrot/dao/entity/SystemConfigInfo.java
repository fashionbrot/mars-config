package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class SystemConfigInfo extends BaseEntity {



    @TableField(value = "env_code")
    private String envCode;

    @TableField(value = "app_name")
    private String appName;

    @TableField(value = "creator")
    private String creator;

    @TableField(value = "modifier")
    private String modifier;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "file_desc")
    private String fileDesc;

    @TableField("json")
    private String json;

    /**
     * 文件类型 TEXT YAML  Properties
     */
    @TableField(value = "file_type")
    private String fileType;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "version")
    private String version;

    private transient Long roleId;

    private transient Long nowUpdateDate;
}
