package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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

@TableName("app_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo extends BaseEntity{

    @TableField(value = "app_name")
    private String appName;

    @TableField(value = "app_desc")
    private String appDesc;

}
