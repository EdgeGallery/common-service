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
    DROP TABLE IF  EXISTS tbl_reverse_proxy;

    -- ----------------------------
    -- Table structure for tbl_reverse_proxy
    -- ----------------------------
    CREATE TABLE IF NOT EXISTS tbl_reverse_proxy (
    dest_host_ip varchar(64) NOT NULL,
    dest_host_protocol varchar(8) NOT NULL,
    dest_host_port int4 NOT NULL,
    local_port int4 NOT NULL,
    next_hop_protocol varchar(8) NULL,
    next_hop_ip varchar(64) NULL,
    next_hop_port int4 NULL,
    link_number int4 NULL,
    hop_index int4 NOT NULL,
    CONSTRAINT tbl_reverse_proxy_pkey PRIMARY KEY (dest_host_ip, dest_host_port)
    );