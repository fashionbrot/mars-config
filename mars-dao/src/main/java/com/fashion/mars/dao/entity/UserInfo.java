package com.fashion.mars.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;


@Data
@TableName("user_info")
public class UserInfo {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("create_date")
    private Date createDate;

    @TableField("update_date")
    private Date updateDate;

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



    private transient String roleName;

    private transient Long roleId;

    public String getCredentialsSalt() {
        return userName + salt;
    }
}
