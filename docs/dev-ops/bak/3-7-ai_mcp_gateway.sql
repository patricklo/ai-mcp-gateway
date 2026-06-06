# ************************************************************
# Sequel Ace SQL dump
# 版本号： 20094
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 主机: 127.0.0.1 (MySQL 8.0.42)
# 数据库: ai_mcp_gateway
# 生成时间: 2026-01-15 00:49:48 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE database if NOT EXISTS `ai_mcp_gateway` default character set utf8mb4 collate utf8mb4_0900_ai_ci;
use `ai_mcp_gateway`;

# 转储表 mcp_gateway
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mcp_gateway`;

CREATE TABLE `mcp_gateway` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gateway_id` varchar(64) NOT NULL COMMENT '网关唯一标识',
  `gateway_name` varchar(128) NOT NULL COMMENT '网关名称',
  `gateway_desc` varchar(512) DEFAULT NULL COMMENT '网关描述',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gateway_id` (`gateway_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MCP网关配置表';

LOCK TABLES `mcp_gateway` WRITE;
/*!40000 ALTER TABLE `mcp_gateway` DISABLE KEYS */;

INSERT INTO `mcp_gateway` (`id`, `gateway_id`, `gateway_name`, `gateway_desc`, `status`, `create_time`, `update_time`)
VALUES
	(1,'gateway_001','员工信息查询网关','用于查询公司员工信息的MCP网关',1,'2026-01-02 13:10:19','2026-01-02 13:10:19');

/*!40000 ALTER TABLE `mcp_gateway` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 mcp_gateway_auth
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mcp_gateway_auth`;

CREATE TABLE `mcp_gateway_auth` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gateway_id` varchar(64) NOT NULL COMMENT '网关ID',
  `api_key` varchar(128) DEFAULT NULL COMMENT 'API密钥',
  `rate_limit` int DEFAULT '1000' COMMENT '速率限制（次/小时）',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_gateway` (`gateway_id`),
  KEY `idx_gateway_id` (`gateway_id`),
  KEY `idx_api_key` (`api_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户网关权限表';

LOCK TABLES `mcp_gateway_auth` WRITE;
/*!40000 ALTER TABLE `mcp_gateway_auth` DISABLE KEYS */;

INSERT INTO `mcp_gateway_auth` (`id`, `gateway_id`, `api_key`, `rate_limit`, `expire_time`, `status`, `create_time`, `update_time`)
VALUES
	(1,'gateway_001','RS590LKPOD8877DDLMFKS4',1000,'2029-01-02 16:44:19',1,'2026-01-02 16:44:19','2026-01-02 16:44:34');

/*!40000 ALTER TABLE `mcp_gateway_auth` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 mcp_protocol_mapping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mcp_protocol_mapping`;

CREATE TABLE `mcp_protocol_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gateway_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属网关ID',
  `tool_id` bigint NOT NULL COMMENT '所属工具ID',
  `mapping_type` varchar(32) NOT NULL COMMENT '映射类型：request-请求参数映射，response-响应数据映射',
  `parent_path` varchar(256) DEFAULT NULL COMMENT '父级路径（如：xxxRequest01，用于构建嵌套结构，根节点为NULL）',
  `field_name` varchar(128) NOT NULL COMMENT '字段名称（如：city、company、name）',
  `mcp_path` varchar(256) NOT NULL COMMENT 'MCP完整路径（如：xxxRequest01.city、xxxRequest01.company.name）',
  `mcp_type` varchar(32) NOT NULL COMMENT 'MCP数据类型：string/number/boolean/object/array',
  `mcp_desc` varchar(512) DEFAULT NULL COMMENT 'MCP字段描述',
  `is_required` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否必填：0-否，1-是（用于生成required数组）',
  `http_path` varchar(256) DEFAULT NULL COMMENT 'HTTP路径（JSON路径，如：company.name 或 data.result，object类型可为空）',
  `http_location` varchar(32) DEFAULT 'body' COMMENT 'HTTP位置：body/query/path/header（仅对request类型有效）',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序（同级字段排序）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tool_id` (`tool_id`),
  KEY `idx_mapping_type` (`mapping_type`),
  KEY `idx_parent_path` (`parent_path`),
  KEY `idx_mcp_path` (`mcp_path`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MCP映射配置表';

LOCK TABLES `mcp_protocol_mapping` WRITE;
/*!40000 ALTER TABLE `mcp_protocol_mapping` DISABLE KEYS */;

INSERT INTO `mcp_protocol_mapping` (`id`, `gateway_id`, `tool_id`, `mapping_type`, `parent_path`, `field_name`, `mcp_path`, `mcp_type`, `mcp_desc`, `is_required`, `http_path`, `http_location`, `sort_order`, `create_time`, `update_time`)
VALUES
	(1,'',1,'request',NULL,'xxxRequest01','xxxRequest01','object',NULL,1,NULL,'body',1,'2026-01-02 13:10:19','2026-01-02 13:10:19'),
	(2,'',1,'request','xxxRequest01','city','xxxRequest01.city','string','城市名称,如果是中文汉字请先转换为汉语拼音,例如北京:beijing',1,'city','body',1,'2026-01-02 13:10:19','2026-01-02 13:10:19'),
	(3,'',1,'request','xxxRequest01','company','xxxRequest01.company','object','公司信息,如果是中文汉字请先转换为汉语拼音,例如北京:jd/alibaba',1,NULL,'body',2,'2026-01-02 13:10:19','2026-01-02 13:10:19'),
	(4,'',1,'request','xxxRequest01.company','name','xxxRequest01.company.name','string','公司名称',1,'company.name','body',1,'2026-01-02 13:10:19','2026-01-02 13:10:19'),
	(5,'',1,'request','xxxRequest01.company','type','xxxRequest01.company.type','string','公司类型',1,'company.type','body',2,'2026-01-02 13:10:19','2026-01-02 13:10:19'),
	(6,'',1,'request',NULL,'xxxRequest02','xxxRequest02','object',NULL,1,NULL,'body',2,'2026-01-02 13:10:19','2026-01-02 13:10:19'),
	(7,'',1,'request','xxxRequest02','employeeCount','xxxRequest02.employeeCount','string','雇员姓名',1,'employeeCount','body',1,'2026-01-02 13:10:19','2026-01-02 13:10:19');

/*!40000 ALTER TABLE `mcp_protocol_mapping` ENABLE KEYS */;
UNLOCK TABLES;


# 转储表 mcp_protocol_registry
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mcp_protocol_registry`;

CREATE TABLE `mcp_protocol_registry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gateway_id` varchar(64) NOT NULL COMMENT '所属网关ID',
  `tool_id` bigint NOT NULL COMMENT '工具ID',
  `tool_name` varchar(128) NOT NULL COMMENT 'MCP工具名称（如：JavaSDKMCPClient_getCompanyEmployee）',
  `tool_type` varchar(32) NOT NULL DEFAULT 'function' COMMENT '工具类型：function/resource',
  `tool_description` varchar(512) DEFAULT NULL COMMENT '工具描述',
  `tool_version` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1.0.0' COMMENT '工具版本',
  `http_url` varchar(512) NOT NULL COMMENT 'HTTP接口地址',
  `http_method` varchar(16) NOT NULL DEFAULT 'POST' COMMENT 'HTTP请求方法：GET/POST/PUT/DELETE',
  `http_headers` text COMMENT 'HTTP请求头（JSON格式）',
  `timeout` int DEFAULT '30000' COMMENT '超时时间（毫秒）',
  `retry_times` tinyint DEFAULT '0' COMMENT '重试次数',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gateway_tool` (`gateway_id`,`tool_name`),
  KEY `idx_gateway_id` (`gateway_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='MCP工具注册表';

LOCK TABLES `mcp_protocol_registry` WRITE;
/*!40000 ALTER TABLE `mcp_protocol_registry` DISABLE KEYS */;

INSERT INTO `mcp_protocol_registry` (`id`, `gateway_id`, `tool_id`, `tool_name`, `tool_type`, `tool_description`, `tool_version`, `http_url`, `http_method`, `http_headers`, `timeout`, `retry_times`, `status`, `create_time`, `update_time`)
VALUES
	(1,'gateway_001',1,'JavaSDKMCPClient_getCompanyEmployee','function','获取公司雇员信息','1.0.0','http://localhost:8701/api/v1/mcp/get_company_employee','POST','{\"Content-Type\": \"application/json\"}',30000,0,1,'2026-01-02 13:10:19','2026-01-15 08:48:12');

/*!40000 ALTER TABLE `mcp_protocol_registry` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
