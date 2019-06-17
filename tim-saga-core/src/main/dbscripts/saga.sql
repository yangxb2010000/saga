/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : localhost:3306
 Source Schema         : saga

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 07/06/2019 00:04:46
*/

CREATE DATABASE /*!32312 IF NOT EXISTS*/`saga` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin */;

use saga;

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
  `create_time` datetime(3) NOT NULL,
  `last_update_time` datetime(3) NOT NULL,
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
  `create_time` datetime(3) NOT NULL,
  `last_update_time` datetime(3) NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` int(11) NOT NULL,
  `application_id` varchar(255) NOT NULL,
  `retried_count` int(11) NOT NULL,
  PRIMARY KEY (`id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
