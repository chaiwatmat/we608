SET NAMES utf8;
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

CREATE DATABASE `we_ir` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `we_ir`;

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `we_frontier`;
CREATE TABLE `we_frontier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(500) NOT NULL,
  `content` longtext,
  `inbound_link` varchar(500) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
