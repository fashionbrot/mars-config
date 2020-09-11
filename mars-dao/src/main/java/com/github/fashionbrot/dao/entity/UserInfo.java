package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Data
@TableName("user_info")
@EqualsAndHashCode(callSuper=false)
public class UserInfo extends BaseEntity {
    private static final long serialVersionUID = -6283733291651727199L;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "password")
    private String password;

    @TableField(value = "real_name")
    private String realName;

    @TableField(value = "salt")
    private String salt;

    @TableField("last_login_time")
    private Date lastLoginTime;

    @TableField("status")
    private int status;

    @TableField("super_admin")
    private Integer superAdmin;

    private transient String roleName;

    private transient Long roleId;

}
