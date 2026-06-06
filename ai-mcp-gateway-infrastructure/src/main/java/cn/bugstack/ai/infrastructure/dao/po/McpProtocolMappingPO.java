package cn.bugstack.ai.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * MCP映射配置表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpProtocolMappingPO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 所属网关ID
     */
    private String gatewayId;
    /**
     * 所属工具ID
     */
    private Long toolId;
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
     * HTTP路径（JSON路径，如：company.name 或 data.result，object类型可为空）
     */
    private String httpPath;
    /**
     * HTTP位置：body/query/path/header（仅对request类型有效）
     */
    private String httpLocation;
    /**
     * 排序顺序（同级字段排序）
     */
    private Integer sortOrder;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
