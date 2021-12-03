/*
 Navicat Premium Data Transfer

 Source Server         : 1
 Source Server Type    : PostgreSQL
 Source Server Version : 100011
 Source Host           : localhost:5432
 Source Catalog        : exampledb
 Source Schema         : mec

 Target Server Type    : PostgreSQL
 Target Server Version : 100011
 File Encoding         : 65001

 Date: 30/12/2019 14:40:23
*/
-- ----------------------------
-- Records of tbl_service_host
-- ----------------------------
insert into tbl_reverse_proxy (dest_host_ip, dest_host_port, local_port, next_hop_protocol,
                                   next_hop_ip, next_hop_port, link_number, hop_index)
    values ('192.168.1.100', 6080, 30111, 'http','192.168.1.101', 30111, 1, 1);
insert into tbl_reverse_proxy (dest_host_ip, dest_host_port, local_port, next_hop_protocol,
                                   next_hop_ip, next_hop_port, link_number, hop_index)
    values ('192.168.1.101', 6080, 30112, '','', 0, 1, 2);