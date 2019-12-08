package com.gitee.mars.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName("system_config_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigHistoryInfo {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("create_date")
    private Date createDate;

    @TableField("update_date")
    private Date updateDate;

    @TableField(value = "file_id")
    private Long fileId;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "env_code")
    private String envCode;

    @TableField(value = "app_name")
    private String appName;


    @TableField(value = "modifier")
    private String modifier;


    @TableField(value = "pre_json")
    private String preJson;

    @TableField("json")
    private String json;

    @TableField("operation_type")
    private int operationType;
}
