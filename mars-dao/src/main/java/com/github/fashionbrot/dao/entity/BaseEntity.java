package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public abstract class BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "create_id",fill = FieldFill.INSERT)
    private Long createId;

    @TableField(value="create_date",fill = FieldFill.INSERT)
    private Date createDate;

    @TableField(value = "update_id",fill = FieldFill.UPDATE)
    private Long updateId;

    @TableField(value = "update_date",fill = FieldFill.UPDATE)
    private Date updateDate;


    @TableLogic(value = "0", delval = "1")
    @TableField(value = "del_flag",fill = FieldFill.INSERT)
    private int delFlag;
}
