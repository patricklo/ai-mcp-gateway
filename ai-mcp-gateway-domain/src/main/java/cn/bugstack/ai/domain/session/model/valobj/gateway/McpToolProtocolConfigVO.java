package cn.bugstack.ai.domain.session.model.valobj.gateway;

import lombok.*;

import java.util.List;

/**
 * 协议配置
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/1/30 20:24
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpToolProtocolConfigVO {

    /**
     * 请求协议配置
     */
    private HTTPConfig httpConfig;

    /**
     * 请求协议映射
     */
    private List<ProtocolMapping> requestProtocolMappings;

    @Data
    public static class HTTPConfig {
        private String httpUrl;
        private String httpHeaders;
        private String httpMethod;
        private Integer timeout;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProtocolMapping {
        /**
         * 映射类型：request-请求参数映射，response-响应数据映射
         */
        private String mappingType;
        /**
         * 父级路径（如：xxxRequest01，用于构建嵌套结构，根节点为NULL）
         */
        private String parentPath;
        /**
         * 字段名称（如：city、company、name）
         */
        private String fieldName;
        /**
         * MCP完整路径（如：xxxRequest01.city、xxxRequest01.company.name）
         */
        private String mcpPath;
        /**
         * MCP数据类型：string/number/boolean/object/array
         */
        private String mcpType;
        /**
         * MCP字段描述
         */
        private String mcpDesc;
        /**
         * 是否必填：0-否，1-是（用于生成required数组）
         */
        private Integer isRequired;
        /**
         * 排序顺序（同级字段排序）
         */
        private Integer sortOrder;
    }

}
