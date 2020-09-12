
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户名',
  `real_name` varchar(20) NOT NULL COMMENT '真实姓名',
  `password` varchar(32) NOT NULL COMMENT '加密密码',
  `salt` varchar(32) NOT NULL COMMENT '密码加盐参数',
  `status` tinyint(2) NOT NULL COMMENT '用户状态',
  `super_admin` tinyint(1) DEFAULT '0' COMMENT '是否是超级管理员 1超级 0普通',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='用户信息表';
ALTER TABLE user_info ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `role_info`;
CREATE TABLE `role_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `role_code` varchar(30) NOT NULL COMMENT '角色标识',
  `role_name` varchar(30) NOT NULL COMMENT '角色描述',
  `status` int(2) NOT NULL COMMENT '权限状态',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';
ALTER TABLE role_info ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `user_role_relation`;
CREATE TABLE `user_role_relation` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户-角色关系表';
ALTER TABLE user_role_relation ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `app_info`;
CREATE TABLE `app_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `app_name` varchar(64) NOT NULL COMMENT '应用名称',
  `app_desc` varchar(255) NOT NULL COMMENT '应用说明',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用表';
ALTER TABLE app_info ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `env_info`;
CREATE TABLE `env_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `env_code` varchar(64) NOT NULL COMMENT '环境code',
  `env_name` varchar(64) NOT NULL COMMENT '环境名称',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='环境表';
ALTER TABLE env_info ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `system_config_info`;
CREATE TABLE `system_config_info` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `app_name` varchar(32) NOT NULL COMMENT '应用名称',
  `env_code` varchar(32) NOT NULL COMMENT '环境code',
  `creator` varchar(32) NOT NULL COMMENT '创建人',
  `modifier` varchar(32) DEFAULT NULL COMMENT '修改人',
  `file_name` varchar(32) NOT NULL COMMENT '文件名称',
  `file_desc` varchar(1000) DEFAULT NULL COMMENT '文件描述',
  `file_type` varchar(16) NOT NULL DEFAULT '5' COMMENT '文件类型 TEXT JSON XML YAML HTML Properties',
  `json` text NOT NULL COMMENT '配置文件内容',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '发布状态 1已发布 0未发布',
  `version` varchar(32) NOT NULL COMMENT 'version',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`),
  KEY `idx_envcode_appname` (`env_code`,`app_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='应用系统配置表';
ALTER TABLE system_config_info ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `system_config_history`;
CREATE TABLE `system_config_history` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `file_id` bigint(11) unsigned NOT NULL COMMENT '文件id',
  `file_name` varchar(32) NOT NULL COMMENT '文件名称',
  `app_name` varchar(32) NOT NULL COMMENT '应用名称',
  `env_code` varchar(32) NOT NULL COMMENT '环境code',
  `json` text DEFAULT NULL COMMENT '配置文件内容-修改前',
  `pre_json` text DEFAULT NULL COMMENT '配置文件内容-修改后',
  `operation_type` int(2) unsigned NOT NULL COMMENT '操作类型',
  `modifier` varchar(32) DEFAULT NULL COMMENT '修改人',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`),
  KEY `idx_envcode_appname` (`env_code`,`app_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='应用系统配置历史表';
ALTER TABLE system_config_history ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `menu_name` varchar(16) NOT NULL COMMENT '菜单名称',
  `menu_level` int(3) unsigned NOT NULL COMMENT '菜单级别',
  `menu_url` varchar(255) DEFAULT NULL COMMENT '菜单url',
  `parent_menu_id` bigint(11) unsigned NOT NULL COMMENT '父菜单id',
  `priority` int(5) unsigned NOT NULL COMMENT '显示优先级',
  `code` varchar(64) DEFAULT NULL COMMENT '权限code',
  `create_id` bigint(11) DEFAULT NULL COMMENT '创建者id',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='菜单表';
ALTER TABLE menu ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `menu_role_relation`;
CREATE TABLE `menu_role_relation` (
  `menu_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜单-角色关系表';
ALTER TABLE menu_role_relation ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `system_config_role_relation`;
CREATE TABLE `system_config_role_relation` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `system_config_id` bigint(20) NOT NULL COMMENT '动态配置ID',
  `view_status` int(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `push_status` int(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `edit_status` int(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `delete_status` int(2) NOT NULL DEFAULT '0' COMMENT '权限状态 1有权限 0无权限',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='动态配置-角色关系表';
ALTER TABLE system_config_role_relation ADD INDEX index_del_flag (del_flag);


INSERT INTO `user_info` (`id`, `user_name`, `real_name`, `password`, `salt`, `status`, `super_admin`, `last_login_time`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`) VALUES ('1', 'mars', 'mars', 'f1a65d566b294b8db222cf61b3b28f72', '3bb81260d3941f5818e72dff4b1342f8', '1', '1', NULL, '1', '2020-09-11 23:40:18', NULL, NULL, '0');

INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('1', '用户管理', '1', '2019-12-08 13:29:27', '2019-12-08 13:29:54', '', '0', '1');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('2', '用户列表', '2', '2019-12-08 13:29:49', NULL, '/user/index', '1', '2');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('3', '菜单管理', '1', '2019-12-08 14:20:46', NULL, '', '0', '3');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('4', '菜单列表', '2', '2019-12-08 14:21:08', NULL, '/admin/menu/index', '3', '4');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('5', '应用环境管理', '1', '2019-12-08 15:26:24', NULL, '', '0', '5');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('6', '应用列表', '2', '2019-12-08 15:26:49', NULL, '/app/index', '5', '6');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('7', '环境列表', '2', '2019-12-08 15:27:07', NULL, '/env/index', '5', '7');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('8', '权限管理', '1', '2019-12-08 15:56:09', NULL, '', '0', '10');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('9', '权限列表', '2', '2019-12-08 15:56:37', NULL, '/role/index', '8', '11');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('10', '配置管理', '1', '2019-12-08 18:33:18', NULL, '', '0', '20');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('11', '配置列表', '2', '2019-12-08 18:33:46', NULL, '/system/index', '10', '21');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `create_date`, `update_date`, `menu_url`, `parent_menu_id`, `priority`) VALUES ('12', '配置历史记录', '2', '2019-12-08 18:34:12', NULL, '/system/history-index', '10', '22');


