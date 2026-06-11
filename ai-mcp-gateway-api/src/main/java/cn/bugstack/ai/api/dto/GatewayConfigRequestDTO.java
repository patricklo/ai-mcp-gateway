package cn.bugstack.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 网关配置请求对象
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/24 08:04
 */
public class GatewayConfigRequestDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayConfig {
        /** 网关唯一标识 */
        private String gatewayId;
        /** 网关名称 */
        private String gatewayName;
        /** 网关描述 */
        private String gatewayDesc;
        /** 协议版本 */
        private String version;
        /** 校验状态：0-禁用，1-启用 */
        private Integer auth;
        /** 网关状态：0-不校验，1-强校验 */
        private Integer status;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayProtocolImport {
        private String openApiJson;
        private List<String> endpoints;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayToolConfig {
        /** 所属网关ID */
        private String gatewayId;
        /** 工具ID */
        private Long toolId;
        /** MCP工具名称（如：JavaSDKMCPClient_getCompanyEmployee） */
        private String toolName;
        /** 工具类型：function/resource */
        private String toolType;
        /** 工具描述 */
        private String toolDescription;
        /** 工具版本 */
        private String toolVersion;
        /** 协议ID */
        private Long protocolId;
        /** 协议类型；http、dubbo、rabbitmq */
        private String protocolType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayProtocol {
        /** 协议列表数据 */
        private List<HTTPProtocol> httpProtocols;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class HTTPProtocol {
            private Long protocolId;
            private String httpUrl;
            private String httpHeaders;
            private String httpMethod;
            private Integer timeout;
            private List<ProtocolMapping> mappings;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProtocolMapping {
            /** 映射类型：request-请求参数映射，response-响应数据映射 */
            private String mappingType;
            /** 父级路径（如：xxxRequest01，用于构建嵌套结构，根节点为NULL） */
            private String parentPath;
            /** 字段名称（如：city、company、name） */
            private String fieldName;
            /** MCP完整路径（如：xxxRequest01.city、xxxRequest01.company.name） */
            private String mcpPath;
            /** MCP数据类型：string/number/boolean/object/array */
            private String mcpType;
            /** MCP字段描述 */
            private String mcpDesc;
            /** 是否必填：0-否，1-是（用于生成required数组） */
            private Integer isRequired;
            /** 排序顺序（同级字段排序） */
            private Integer sortOrder;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GatewayAuth {
        /** 网关ID */
        private String gatewayId;
        /** 速率限制（次/小时） */
        private Integer rateLimit;
        /** 过期时间 */
        private Date expireTime;
    }

}
