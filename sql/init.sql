CREATE SCHEMA mars AUTHORIZATION mars;

CREATE TABLE `user_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户名',
  `real_name` varchar(20) NOT NULL COMMENT '真实姓名',
  `password` varchar(32) NOT NULL COMMENT '加密密码',
  `salt` varchar(32) NOT NULL COMMENT '密码加盐参数',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `status` tinyint(2) NOT NULL COMMENT '用户状态',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='用户信息表';

CREATE TABLE `role_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `role_code` varchar(30) NOT NULL COMMENT '角色标识',
  `role_name` varchar(30) NOT NULL COMMENT '角色描述',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `status` int(2) NOT NULL COMMENT '权限状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

CREATE TABLE `user_role_relation` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户-角色关系表';



CREATE TABLE `app_info` (
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `app_name` varchar(64) NOT NULL COMMENT '应用名称',
  `app_desc` varchar(255) NOT NULL COMMENT '应用说明',
  PRIMARY KEY (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用表';

CREATE TABLE `env_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `env_code` varchar(64) NOT NULL COMMENT '环境code',
  `env_name` varchar(64) NOT NULL COMMENT '环境名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='环境表';



CREATE TABLE `system_config_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `app_name` varchar(32) NOT NULL COMMENT '应用名称',
  `env_code` varchar(32) NOT NULL COMMENT '环境code',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `modifier` varchar(32) DEFAULT NULL COMMENT '修改人',
  `file_name` varchar(32) NOT NULL COMMENT '文件名称',
  `file_desc` varchar(1000) DEFAULT NULL COMMENT '文件描述',
  `file_type` varchar(16) NOT NULL DEFAULT '5' COMMENT '文件类型 TEXT JSON XML YAML HTML Properties',
  `json` text NOT NULL COMMENT '配置文件内容',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '发布状态 1已发布 0未发布',
  `version` varchar(32) NOT NULL COMMENT 'version',
  PRIMARY KEY (`id`),
  KEY `idx_envcode_appname` (`env_code`,`app_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='应用系统配置表';


CREATE TABLE `system_config_history` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `file_id` bigint(11) unsigned NOT NULL COMMENT '文件id',
  `file_name` varchar(32) NOT NULL COMMENT '文件名称',
  `app_name` varchar(32) NOT NULL COMMENT '应用名称',
  `env_code` varchar(32) NOT NULL COMMENT '环境code',
  `json` text DEFAULT NULL COMMENT '配置文件内容-修改前',
  `pre_json` text DEFAULT NULL COMMENT '配置文件内容-修改后',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `operation_type` int(2) unsigned NOT NULL COMMENT '操作类型',
  PRIMARY KEY (`id`),
  KEY `idx_envcode_appname` (`env_code`,`app_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='应用系统配置历史表';

CREATE TABLE `system_config_role_relation` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `system_config_id` bigint(20) NOT NULL COMMENT '动态配置ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `view_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `push_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `edit_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `delete_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='动态配置-角色关系表';


CREATE TABLE `menu` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `menu_name` varchar(16) NOT NULL COMMENT '菜单名称',
  `menu_level` int(3) unsigned NOT NULL COMMENT '菜单级别',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `menu_url` varchar(255) DEFAULT NULL COMMENT '菜单url',
  `parent_menu_id` bigint(11) unsigned NOT NULL COMMENT '父菜单id',
  `priority` int(5) unsigned NOT NULL COMMENT '显示优先级',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='菜单表';

CREATE TABLE `menu_role_relation` (
  `menu_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜单-角色关系表';


CREATE TABLE `system_config_role_relation` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `system_config_id` bigint(20) NOT NULL COMMENT '动态配置ID',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_date` datetime DEFAULT NULL COMMENT '修改时间',
  `view_status` int(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `push_status` int(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `edit_status` int(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `delete_status` int(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='动态配置-角色关系表';
