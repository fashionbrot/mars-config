
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='应用系统配置表';
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='应用系统配置历史表';
ALTER TABLE system_config_history ADD INDEX index_del_flag (del_flag);


DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `menu_name` varchar(16) NOT NULL COMMENT '菜单名称',
  `menu_level` int(3) unsigned NOT NULL COMMENT '菜单级别',
  `menu_url` varchar(255) DEFAULT NULL COMMENT '菜单url',
  `parent_menu_id` bigint(11) unsigned DEFAULT '0' COMMENT '父菜单id',
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

INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('1', '用户管理', '1', '', '0', '100', NULL, '2019-12-08 13:29:27', '1', '2020-09-12 21:19:14', '0', NULL);
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('2', '用户列表', '2', '/user/index', '1', '101', NULL, '2019-12-08 13:29:49', '1', '2020-09-12 21:19:23', '0', NULL);
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('13', '用户列表-修改密码', '3', '', '2', '102', '1', '2020-09-12 21:43:55', NULL, NULL, '0', 'user:reset:pwd');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('14', '用户列表-新增用户', '3', '', '2', '103', '1', '2020-09-12 21:45:05', NULL, NULL, '0', 'user:list:add');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('15', '用户列表-编辑用户', '3', '', '2', '104', '1', '2020-09-12 21:45:41', NULL, NULL, '0', 'user:list:edit');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('16', '用户列表-删除用户', '3', '', '2', '105', '1', '2020-09-12 21:54:11', NULL, NULL, '0', 'user:list:del');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('17', '用户列表-查看详情', '3', '', '2', '106', '1', '2020-09-12 21:55:55', '1', '2020-09-12 22:39:10', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('18', '用户列表-查询全部', '3', '', '2', '107', '1', '2020-09-12 21:56:51', NULL, NULL, '0', 'user:list:list');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('3', '菜单管理', '1', '', '0', '200', NULL, '2019-12-08 14:20:46', '1', '2020-09-12 22:34:12', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('4', '菜单列表', '2', '/admin/menu/index', '3', '201', NULL, '2019-12-08 14:21:08', '1', '2020-09-12 22:34:21', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('19', '菜单列表-新增', '3', '', '4', '202', '1', '2020-09-12 22:34:05', NULL, NULL, '0', 'menu:list:add');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('20', '菜单列表-编辑', '3', '', '4', '203', '1', '2020-09-12 22:35:35', NULL, NULL, '0', 'menu:list:edit');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('21', '菜单列表-删除', '3', '', '4', '204', '1', '2020-09-12 22:36:17', NULL, NULL, '0', 'menu:list:del');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('22', '菜单列表-查看详情', '3', '', '4', '205', '1', '2020-09-12 22:37:23', NULL, NULL, '0', 'menu:list:info');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('23', '菜单列表-列表', '3', '', '4', '206', '1', '2020-09-12 22:37:59', NULL, NULL, '0', 'menu:list:list');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('5', '应用环境管理', '1', '', '0', '300', NULL, '2019-12-08 15:26:24', '1', '2020-09-12 22:51:10', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('6', '应用列表', '2', '/app/index', '5', '301', NULL, '2019-12-08 15:26:49', '1', '2020-09-12 22:51:23', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('24', '应用列表-列表', '3', '', '6', '302', '1', '2020-09-12 22:52:33', NULL, NULL, '0', 'app:list:list');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('25', '应用列表-新增', '3', '', '6', '303', '1', '2020-09-12 22:54:21', NULL, NULL, '0', 'app:list:add');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('26', '应用列表-编辑', '3', '', '6', '304', '1', '2020-09-12 22:54:52', NULL, NULL, '0', 'app:list:edit');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('27', '应用列表-删除', '3', '', '6', '305', '1', '2020-09-12 22:55:33', NULL, NULL, '0', 'app:list:del');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('28', '应用列表-详情', '3', '', '6', '306', '1', '2020-09-12 22:56:17', NULL, NULL, '0', 'app:list:info');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('7', '环境列表', '2', '/env/index', '5', '350', NULL, '2019-12-08 15:27:07', '1', '2020-09-12 22:51:41', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('29', '环境列表-列表', '3', '', '7', '351', '1', '2020-09-12 23:19:14', NULL, NULL, '0', 'env:list:list');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('30', '环境列表-详情', '3', '', '7', '352', '1', '2020-09-12 23:19:51', NULL, NULL, '0', 'env:list:info');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('31', '环境列表-新增', '3', '', '7', '353', '1', '2020-09-12 23:21:22', NULL, NULL, '0', 'env:list:add');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('32', '环境列表-编辑', '3', '', '7', '354', '1', '2020-09-12 23:21:50', NULL, NULL, '0', 'env:list:edit');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('33', '环境列表-删除', '3', '', '7', '355', '1', '2020-09-12 23:22:21', NULL, NULL, '0', 'env:list:del');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('8', '权限管理', '1', '', '0', '400', NULL, '2019-12-08 15:56:09', '1', '2020-09-12 23:32:16', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('9', '权限列表', '2', '/role/index', '8', '401', NULL, '2019-12-08 15:56:37', '1', '2020-09-12 23:32:21', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('34', '权限列表-列表', '3', '', '9', '402', '1', '2020-09-12 23:32:52', NULL, NULL, '0', 'role:list:list');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('35', '权限列表-详情', '3', '', '9', '403', '1', '2020-09-12 23:33:24', NULL, NULL, '0', 'role:list:info');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('36', '权限列表-新增', '3', '', '9', '404', '1', '2020-09-12 23:33:56', NULL, NULL, '0', 'role:list:add');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('37', '权限列表-编辑', '3', '', '9', '405', '1', '2020-09-12 23:34:24', NULL, NULL, '0', 'role:list:edit');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('38', '权限列表-删除', '3', '', '9', '406', '1', '2020-09-12 23:35:00', NULL, NULL, '0', 'role:list:del');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('39', '权限列表-菜单权限', '3', '', '9', '407', '1', '2020-09-12 23:39:59', NULL, NULL, '0', 'role:list:update:menu');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('40', '权限列表-动态配置权限', '3', '', '9', '408', '1', '2020-09-12 23:43:09', '1', '2020-09-12 23:43:42', '0', 'role:list:update:role');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('10', '配置管理', '1', '', '0', '500', NULL, '2019-12-08 18:33:18', '1', '2020-09-12 23:51:30', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('11', '配置列表', '2', '/system/index', '10', '501', NULL, '2019-12-08 18:33:46', '1', '2020-09-12 23:51:43', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('41', '配置列表-列表', '3', '', '11', '502', '1', '2020-09-12 23:52:21', NULL, NULL, '0', 'config:list:list');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('42', '配置列表-详情', '3', '', '11', '503', '1', '2020-09-12 23:52:57', NULL, NULL, '0', 'config:list:info');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('43', '配置列表-新增', '3', '', '11', '504', '1', '2020-09-12 23:53:27', NULL, NULL, '0', 'config:list:add');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('44', '配置列表-编辑', '3', '', '11', '505', '1', '2020-09-12 23:54:09', NULL, NULL, '0', 'config:list:edit');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('45', '配置列表-删除', '3', '', '11', '506', '1', '2020-09-12 23:54:31', NULL, NULL, '0', 'config:list:del');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('46', '配置列表-发布', '3', '', '11', '507', '1', '2020-09-12 23:55:02', NULL, NULL, '0', 'config:list:publish');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('12', '配置历史记录', '2', '/system/history-index', '10', '550', NULL, '2019-12-08 18:34:12', '1', '2020-09-12 23:51:52', '0', '');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('47', '配置历史记录-列表', '3', '', '12', '551', '1', '2020-09-13 00:01:35', NULL, NULL, '0', 'history:list:list');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('48', '配置历史记录-详情', '3', '', '12', '552', '1', '2020-09-13 00:02:11', NULL, NULL, '0', 'history:list:info');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('49', '配置历史记录-删除', '3', '', '12', '553', '1', '2020-09-13 00:02:54', NULL, NULL, '0', 'history:list:del');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`, `code`) VALUES ('50', '配置历史记录-回滚', '3', '', '12', '554', '1', '2020-09-13 00:03:30', NULL, NULL, '0', 'history:list:rollback');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `code`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`) VALUES ('51', '模板管理', '1', '', '0', '600', '', '1', '2020-10-12 15:40:12', NULL, NULL, '0');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `code`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`) VALUES ('52', '模板列表', '2', '/admin/template/index', '51', '601', '', '1', '2020-10-12 15:40:41', NULL, NULL, '0');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `code`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`) VALUES ('53', '属性列表', '2', '/admin/property/index', '51', '602', '', '1', '2020-10-12 16:53:58', NULL, NULL, '0');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `code`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`) VALUES ('54', '配置数据管理', '1', '', '0', '700', '', '1', '2020-10-18 00:07:29', '1', '2020-10-18 00:07:40', '0');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `code`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`) VALUES ('55', '配置数据列表', '2', '/admin/config/value/index', '54', '701', '', '1', '2020-10-18 00:08:06', NULL, NULL, '0');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `code`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`) VALUES ('56', '环境变量列表', '2', '/admin/variable/index', '51', '690', '', '1', '2020-10-13 10:39:43', NULL, NULL, '0');
INSERT INTO `menu` (`id`, `menu_name`, `menu_level`, `menu_url`, `parent_menu_id`, `priority`, `code`, `create_id`, `create_date`, `update_id`, `update_date`, `del_flag`) VALUES ('57', '配置数据记录', '2', '/admin/config/record/index', '54', '800', '', '1', '2020-10-14 17:14:49', '1', '2020-10-22 17:27:04', '0');





CREATE TABLE `template` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `app_name` varchar(64) NOT NULL COMMENT '应用名',
  `template_key` varchar(64) NOT NULL COMMENT '模板key',
  `template_name` varchar(64) NOT NULL COMMENT '模板名称',
  `template_desc` varchar(1000) DEFAULT NULL COMMENT '模板描述',
  `template_url` varchar(500) DEFAULT NULL COMMENT '模板示例图片地址',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模板表';

CREATE TABLE `property` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `property_name` varchar(64) NOT NULL COMMENT '属性名称',
  `property_key` varchar(64) NOT NULL COMMENT '属性key',
  `property_type` varchar(64) NOT NULL COMMENT '属性类型',
  `column_length` int(4) NOT NULL COMMENT '属性长度',
  `label_type` varchar(64) NOT NULL COMMENT 'html标签类型',
  `label_value` text COMMENT 'html 标签值',
  `app_name` varchar(32) NOT NULL COMMENT '应用名称',
  `variable_key` varchar(32) DEFAULT NULL COMMENT '常量key',
  `template_key` varchar(32) DEFAULT NULL COMMENT '模板key ，公共属性为空，指定模板属性不为空',
  `attribute_type` tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT '0 公共属性 1 模板属性',
  `priority` tinyint(5) NOT NULL DEFAULT '0' COMMENT '显示优先级',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='属性表';

INSERT INTO `property` (`column_length`,`property_name`, `property_key`, `property_type`, `label_type`, `label_value`, `app_name`, `variable_key`, `template_key`, `attribute_type`, `create_id`, `create_date`,  `del_flag`, `priority`) VALUES (64, '标题', 'title', 'varchar', 'input', '', '-1', '', '-1', '1', '1', '2020-10-14 14:56:59', '0', '1');
INSERT INTO `property` (`column_length`,`property_name`, `property_key`, `property_type`, `label_type`, `label_value`, `app_name`, `variable_key`, `template_key`, `attribute_type`, `create_id`, `create_date`, `del_flag`, `priority`) VALUES (0,'开始时间', 'startDate', 'datetime', 'input', '', '-1', '', '-1', '0', '1', '2020-10-14 14:58:03', '0', '2');
INSERT INTO `property` (`column_length`,`property_name`, `property_key`, `property_type`, `label_type`, `label_value`, `app_name`, `variable_key`, `template_key`, `attribute_type`, `create_id`, `create_date`,  `del_flag`, `priority`) VALUES (0, '结束时间', 'endDate', 'datetime', 'input', '', '-1', '', '-1', '1', '1', '2020-10-14 14:58:47', '0', '3');



CREATE TABLE `template_property_relation` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `template_key` varchar(32) NOT NULL COMMENT '模板key',
  `property_key` varchar(32) NOT NULL COMMENT '属性key',
  `app_name` varchar(32) NOT NULL COMMENT '应用名称',
  `priority` tinyint(5) NOT NULL DEFAULT '0' COMMENT '显示优先级',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='模板属性关系表';



CREATE TABLE `env_variable_relation` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `env_code` varchar(32) NOT NULL COMMENT '环境code',
  `variable_value` varchar(255) NOT NULL COMMENT '常量值',
  `variable_key` varchar(32) NOT NULL COMMENT '常量key',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='常量和环境关系表';


CREATE TABLE `env_variable` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `variable_name` varchar(32) NOT NULL COMMENT '变量名称',
  `variable_desc` varchar(255) DEFAULT NULL COMMENT '变量说明',
  `variable_key` varchar(32) NOT NULL COMMENT '变量key',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='常量表';


CREATE TABLE `config_value` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `env_code` varchar(32) NOT NULL COMMENT '环境code',
  `app_name` varchar(32) NOT NULL COMMENT '应用名',
  `template_key` varchar(32) NOT NULL COMMENT '模板key',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态 1开启 0关闭',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `priority` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '优先级',
  `release_status` tinyint(1) DEFAULT '0' COMMENT '发布状态 1已发布 0修改 2已删除 3新增',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名',
  `json` text DEFAULT NULL COMMENT '实例json',
  `temp_json` text DEFAULT NULL COMMENT 'temp json',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`),
  KEY `index_eat` (`env_code`,`app_name`,`template_key`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='配置数据表';



CREATE TABLE `config_record` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `env_code` varchar(32) NOT NULL COMMENT '环境code',
  `app_name` varchar(32) NOT NULL COMMENT '应用名',
  `template_key` varchar(32) NOT NULL COMMENT '模板key',
  `config_id` bigint(11) DEFAULT  NULL COMMENT '配置id',
  `json` text DEFAULT NULL COMMENT '实例json',
  `new_json` text DEFAULT NULL COMMENT '实例json',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名',
  `operation_type` tinyint(1) DEFAULT '0' COMMENT '操作类型 2编辑 3:删除',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='配置数据记录表';



CREATE TABLE `value_data` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `data_type` tinyint(2) NOT NULL COMMENT '0 导入  1导出',
  `json` longtext COMMENT '导出导入json数据',
  `env_code` varchar(32) NOT NULL COMMENT '环境code',
  `app_name` varchar(32) DEFAULT NULL COMMENT '应用名',
  `user_name` varchar(32) DEFAULT NULL COMMENT '用户名',
  `template_key` varchar(1000) DEFAULT NULL COMMENT '模板key',
  `create_id` bigint(11) NOT NULL COMMENT '创建者id',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_id` bigint(11) DEFAULT NULL COMMENT '最近更新者id',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='导入导出记录表';

CREATE TABLE `config_release` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `env_code` varchar(32) NOT NULL COMMENT '环境code',
  `app_name` varchar(32) NOT NULL COMMENT '应用名',
  `template_keys` varchar(255) DEFAULT NULL COMMENT '模板keys',
  `update_date` datetime DEFAULT NULL COMMENT '最近更新时间',
  `release_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志位 1删除 0未删除',
  PRIMARY KEY (`id`),
  KEY `index_envCode_appName` (`env_code`,`app_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='配置数据发布表';