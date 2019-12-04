package com.fashion.mars.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("system_config_role_relation")
public class SystemConfigRoleRelation implements Serializable {
    private static final long serialVersionUID = 6099317626818127004L;


    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("create_date")
    private Date createDate;

    @TableField("update_date")
    private Date updateDate;

    @TableField("system_config_id")
    private Long systemConfigId;

    @TableField("role_id")
    private Long roleId;

    @TableField("view_status")
    private int viewStatus;

    @TableField("push_status")
    private int pushStatus;

    @TableField("edit_status")
    private int editStatus;

    @TableField("delete_status")
    private int deleteStatus;

    private transient String fileName;

}
