/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : localhost:3306
 Source Schema         : saga-test

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 07/06/2019 00:04:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_saga_participant
-- ----------------------------
DROP TABLE IF EXISTS `t_saga_participant`;
CREATE TABLE `t_saga_participant` (
  `id` varchar(50) NOT NULL,
  `transaction_id` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `create_time` bigint(20) NOT NULL,
  `last_update_time` bigint(20) NOT NULL,
  `status` int(11) NOT NULL,
  `cancel_invocation_context` mediumblob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_saga_transaction
-- ----------------------------
DROP TABLE IF EXISTS `t_saga_transaction`;
CREATE TABLE `t_saga_transaction` (
  `id` varchar(50) NOT NULL,
  `create_time` bigint(20) NOT NULL,
  `last_update_time` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` int(11) NOT NULL,
  `application_id` varchar(255) NOT NULL,
  `retried_count` int(11) NOT NULL,
  PRIMARY KEY (`id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
