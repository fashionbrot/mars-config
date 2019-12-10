package com.gitee.mars.dao.entity;

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
import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@TableName("menu")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu implements Serializable {

    private static final long serialVersionUID = -8331064931930047236L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("create_date")
    private Date createDate;

    @TableField("update_date")
    private Date updateDate;
    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;
    /**
     * 菜单级别
     * 1 一级菜单
     * 2 二级菜单
     * 3 按钮
     */
    @TableField("menu_level")
    private Integer menuLevel;
    /**
     * 菜单名称
     */
    @TableField("menu_name")
    private String menuName;
    /**
     * 菜单 url
     */
    @TableField("menu_url")
    private String menuUrl;
    /**
     * 父级 菜单id
     */
    @TableField("parent_menu_id")
    private Long parentMenuId;

    /**
     * 父级 菜单名称
     */
    private transient String parentMenuName;

    private transient List<Menu> childMenu;

    private transient int active;
}
