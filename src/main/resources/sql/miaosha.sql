/*
 Navicat Premium Data Transfer

 Source Server         : 本机
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : miaosha

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 28/04/2021 18:50:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `goods_name` varchar(16) DEFAULT NULL COMMENT '商品名称',
  `goods_title` varchar(64) DEFAULT NULL COMMENT '商品标题',
  `goods_img` varchar(64) DEFAULT NULL COMMENT '商品的图片',
  `goods_detail` longtext COMMENT '商品的详情介绍',
  `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
  `goods_stock` int(11) DEFAULT '0' COMMENT '商品库存，-1表示没有限制',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of goods
-- ----------------------------
BEGIN;
INSERT INTO `goods` VALUES (1, 'iphoneX', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', '/static/img/iphonex.png', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', 8765.00, 10000);
INSERT INTO `goods` VALUES (2, '华为Meta9', '华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '/static/img/meta10.png', '华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', 3212.00, -1);
COMMIT;

-- ----------------------------
-- Table structure for miaosha_goods
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_goods`;
CREATE TABLE `miaosha_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品Id',
  `miaosha_price` decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
  `stock_count` int(11) DEFAULT NULL COMMENT '库存数量',
  `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of miaosha_goods
-- ----------------------------
BEGIN;
INSERT INTO `miaosha_goods` VALUES (1, 1, 0.01, 9, '2021-01-05 15:18:00', '2021-11-13 14:00:18');
INSERT INTO `miaosha_goods` VALUES (2, 2, 0.01, 10, '2017-11-12 14:00:14', '2025-11-13 14:00:24');
COMMIT;

-- ----------------------------
-- Table structure for miaosha_order
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_order`;
CREATE TABLE `miaosha_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_uid_gid` (`user_id`,`goods_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of miaosha_order
-- ----------------------------
BEGIN;
INSERT INTO `miaosha_order` VALUES (12, 13708516555, 21, 1);
COMMIT;

-- ----------------------------
-- Table structure for miaosha_user
-- ----------------------------
DROP TABLE IF EXISTS `miaosha_user`;
CREATE TABLE `miaosha_user` (
  `id` bigint(20) NOT NULL COMMENT '用户ID，手机号码',
  `nickname` varchar(255) NOT NULL,
  `password` varchar(32) DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt) + salt)',
  `salt` varchar(10) DEFAULT NULL,
  `head` varchar(128) DEFAULT NULL COMMENT '头像，云存储的ID',
  `register_date` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime DEFAULT NULL COMMENT '上蔟登录时间',
  `login_count` int(11) DEFAULT '0' COMMENT '登录次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of miaosha_user
-- ----------------------------
BEGIN;
INSERT INTO `miaosha_user` VALUES (13708516555, 'coco', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c4d', NULL, NULL, NULL, 0);
INSERT INTO `miaosha_user` VALUES (13708516556, 'coco', 'b7797cce01b4b131b433b6acf4add449', '1a2b3c4d', NULL, NULL, NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `delivery_addr_id` bigint(20) DEFAULT NULL COMMENT '收获地址ID',
  `goods_name` varchar(16) DEFAULT NULL COMMENT '冗余过来的商品名称',
  `goods_count` int(11) DEFAULT '0' COMMENT '商品数量',
  `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
  `order_channel` tinyint(4) DEFAULT '0' COMMENT '1pc，2android，3ios',
  `status` tinyint(4) DEFAULT '0' COMMENT '订单状态，0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
  `create_date` datetime DEFAULT NULL COMMENT '订单的创建时间',
  `pay_date` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of order_info
-- ----------------------------
BEGIN;
INSERT INTO `order_info` VALUES (21, 13708516555, 1, NULL, 'iphoneX', 1, 0.01, 1, 0, '2021-03-26 08:54:19', NULL);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 'coco');
INSERT INTO `user` VALUES (2, '2222');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
