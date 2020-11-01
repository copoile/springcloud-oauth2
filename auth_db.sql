/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost
 Source Database       : auth_db

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : utf-8

 Date: 11/01/2020 16:41:30 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `sys_authority`
-- ----------------------------
DROP TABLE IF EXISTS `sys_authority`;
CREATE TABLE `sys_authority` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `authority` varchar(125) DEFAULT NULL COMMENT '权限',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='权限表';

-- ----------------------------
--  Records of `sys_authority`
-- ----------------------------
BEGIN;
INSERT INTO `sys_authority` VALUES ('1', 'admin', 'admin'), ('2', 'test', 'test');
COMMIT;

-- ----------------------------
--  Table structure for `sys_client`
-- ----------------------------
DROP TABLE IF EXISTS `sys_client`;
CREATE TABLE `sys_client` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `client_id` varchar(255) DEFAULT NULL COMMENT '客户端id',
  `client_secret` varchar(255) DEFAULT NULL COMMENT '客户端密钥',
  `scopes` varchar(255) DEFAULT NULL COMMENT 'scopes，以英文逗号分隔',
  `resource_ids` varchar(255) DEFAULT NULL COMMENT '资源ids，以英文逗号分隔',
  `grant_types` varchar(255) DEFAULT NULL COMMENT 'grant_types，以英文逗号分隔',
  `redirect_uris` varchar(255) DEFAULT NULL COMMENT '重定向uris，以英文逗号分隔',
  `auto_approve_scopes` varchar(255) DEFAULT NULL COMMENT '授权码模式自动审批scopes，以英文逗号分隔',
  `access_token_validity_seconds` bigint(20) DEFAULT NULL COMMENT 'accessToken有效秒数',
  `refresh_token_validity_seconds` bigint(20) DEFAULT NULL COMMENT 'refreshToken有效秒数',
  `authority_ids` varchar(255) DEFAULT NULL COMMENT '权限ids，以英文逗号分隔',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `sys_client`
-- ----------------------------
BEGIN;
INSERT INTO `sys_client` VALUES ('1', 'web', '$2a$10$9ustgCuVxydWmgVVA7U9..vONYU8n8yrjJLkg5GsuBNCNHeGKbIOe', 'server', 'auth-server,resource-server', 'password,refresh_token,mobile,authorization_code,implicit', 'http://www.baidu.com', 'server', '7200', '36000', '');
COMMIT;

-- ----------------------------
--  Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(125) DEFAULT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
--  Records of `sys_role`
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` VALUES ('1', 'administrator', '管理员');
COMMIT;

-- ----------------------------
--  Table structure for `sys_role_authority_relation`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_authority_relation`;
CREATE TABLE `sys_role_authority_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sys_role_id` int(11) DEFAULT NULL COMMENT '角色id',
  `sys_authority_id` int(11) DEFAULT NULL COMMENT '权限id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='角色权限关联表';

-- ----------------------------
--  Records of `sys_role_authority_relation`
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_authority_relation` VALUES ('1', '1', '1');
COMMIT;

-- ----------------------------
--  Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(125) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `enable` int(1) DEFAULT '1' COMMENT '是否启用，1:是，0:否',
  `phone` bigint(11) DEFAULT NULL COMMENT '手机号',
  `nick_name` varchar(125) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`id`),
  KEY `username_index` (`username`),
  KEY `phone_index` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='系统用户表';

-- ----------------------------
--  Records of `sys_user`
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES ('1', 'admin', '$2a$10$9ustgCuVxydWmgVVA7U9..vONYU8n8yrjJLkg5GsuBNCNHeGKbIOe', '1', '15625295093', '小管家', null);
COMMIT;

-- ----------------------------
--  Table structure for `sys_user_role_relation`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role_relation`;
CREATE TABLE `sys_user_role_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sys_user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `sys_role_id` int(11) DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户角色关联表';

-- ----------------------------
--  Records of `sys_user_role_relation`
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role_relation` VALUES ('1', '1', '1');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
