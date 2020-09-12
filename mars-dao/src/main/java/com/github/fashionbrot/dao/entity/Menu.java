package com.github.fashionbrot.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
@EqualsAndHashCode(callSuper=false)
public class Menu extends BaseEntity{

    private static final long serialVersionUID = -8331064931930047236L;

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
     * 权限code
     */
    private String code;

    /**
     * 父级 菜单名称
     */
    private transient String parentMenuName;

    private transient List<Menu> childMenu;

    private transient int active;

    private transient boolean checked;

    private transient boolean open;

    private transient String name;
}
