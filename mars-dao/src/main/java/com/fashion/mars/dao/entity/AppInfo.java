package com.fashion.mars.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@TableName("app_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo implements Serializable{
    private static final long serialVersionUID = 6900049095288795420L;

    @TableField(value = "app_name")
    private String appName;

    @TableField(value = "app_desc")
    private String appDesc;


    @TableField(value = "create_date")
    private Date createTime;

    @TableField(value = "update_date")
    private Date updateTime;
}
