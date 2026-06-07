package cn.bugstack.ai.domain.session.service.message.handler.impl;

import cn.bugstack.ai.domain.session.adapter.repository.ISessionRepository;
import cn.bugstack.ai.domain.session.model.valobj.McpSchemaVO;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpToolConfigVO;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpToolProtocolConfigVO;
import cn.bugstack.ai.domain.session.service.message.handler.IRequestHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 返回服务器支持的工具列表
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/20 11:29
 */
@Slf4j
@Service("toolsListHandler")
public class ToolsListHandler implements IRequestHandler {

    @Resource
    private ISessionRepository repository;

    /**
     * {
     * "tools": [
     * {
     * "description": "获取公司雇员信息",
     * "inputSchema": {
     * "additionalProperties": false,
     * "properties": {
     * "xxxRequest01": {
     * "type": "object",
     * "properties": {
     * "city": {
     * "type": "string",
     * "description": "城市名称,如果是中文汉字请先转换为汉语拼音,例如北京:beijing"
     * },
     * "company": {
     * "type": "object",
     * "properties": {
     * "name": {
     * "type": "string",
     * "description": "公司名称"
     * },
     * "type": {
     * "type": "string",
     * "description": "公司类型"
     * }
     * },
     * "required": [
     * "name",
     * "type"
     * ],
     * "description": "公司信息,如果是中文汉字请先转换为汉语拼音,例如北京:jd/alibaba"
     * }
     * },
     * "required": [
     * "city",
     * "company"
     * ]
     * },
     * "xxxRequest02": {
     * "type": "object",
     * "properties": {
     * "employeeCount": {
     * "type": "string",
     * "description": "雇员姓名"
     * }
     * },
     * "required": [
     * "employeeCount"
     * ]
     * }
     * },
     * "required": [
     * "xxxRequest01",
     * "xxxRequest02"
     * ],
     * "type": "object"
     * },
     * "name": "getCompanyEmployee"
     * }
     * ]
     * }
     */
    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {

        // 1. 查询网关（gatewayId）下的工具列表配置
        List<McpToolConfigVO> mcpToolConfigVOS = repository.queryMcpGatewayToolConfigListByGatewayId(gatewayId);

        // 2. 构建工具列表
        List<McpSchemaVO.Tool> tools = buildTools(mcpToolConfigVOS);

        return new McpSchemaVO.JSONRPCResponse("2.0", message.id(), Map.of(
                "tools", tools
        ), null);
    }

    private List<McpSchemaVO.Tool> buildTools(List<McpToolConfigVO> toolConfigs) {
        List<McpSchemaVO.Tool> tools = new ArrayList<>();

        for (McpToolConfigVO toolConfigVO : toolConfigs) {
            McpToolProtocolConfigVO mcpToolProtocolConfigVO = toolConfigVO.getMcpToolProtocolConfigVO();
            List<McpToolProtocolConfigVO.ProtocolMapping> configs = mcpToolProtocolConfigVO.getRequestProtocolMappings();

            // 排序
            configs.sort((o1, o2) -> {
                int s1 = o1.getSortOrder() != null ? o1.getSortOrder() : 0;
                int s2 = o2.getSortOrder() != null ? o2.getSortOrder() : 0;
                return Integer.compare(s1, s2);
            });

            // 父子元素 Map parentPath -> List<Children>
            Map<String, List<McpToolProtocolConfigVO.ProtocolMapping>> childrenMap = new HashMap<>();

            List<McpToolProtocolConfigVO.ProtocolMapping> roots = new ArrayList<>();

            for (McpToolProtocolConfigVO.ProtocolMapping config : configs) {
                if (config.getParentPath() == null) {
                    roots.add(config);
                } else {
                    childrenMap.computeIfAbsent(config.getParentPath(), k -> new ArrayList<>()).add(config);
                }
            }

            // 排序
            roots.sort((o1, o2) -> {
                int s1 = o1.getSortOrder() != null ? o1.getSortOrder() : 0;
                int s2 = o2.getSortOrder() != null ? o2.getSortOrder() : 0;
                return Integer.compare(s1, s2);
            });

            // 构建输入结构
            Map<String, Object> properties = new HashMap<>();
            List<String> required = new ArrayList<>();

            for (McpToolProtocolConfigVO.ProtocolMapping root : roots) {
                properties.put(root.getFieldName(), buildProperty(root, childrenMap));
                if (Integer.valueOf(1).equals(root.getIsRequired())) {
                    required.add(root.getFieldName());
                }
            }

            // 获取类型
            String type = roots.size() == 1 ? roots.get(0).getMcpType() : "object";

            // 构造函数
            McpSchemaVO.JsonSchema inputSchema = new McpSchemaVO.JsonSchema(
                    type,
                    properties,
                    required.isEmpty() ? null : required,
                    false,
                    null,
                    null
            );

            // 工具描述
            tools.add(new McpSchemaVO.Tool(toolConfigVO.getToolName(), toolConfigVO.getToolDescription(), inputSchema));
        }

        return tools;
    }

    private Map<String, Object> buildProperty(McpToolProtocolConfigVO.ProtocolMapping current, Map<String, List<McpToolProtocolConfigVO.ProtocolMapping>> childrenMap) {
        Map<String, Object> property = new HashMap<>();
        property.put("type", current.getMcpType());
        if (current.getMcpDesc() != null) {
            property.put("description", current.getMcpDesc());
        }

        // 校验孩子元素
        List<McpToolProtocolConfigVO.ProtocolMapping> children = childrenMap.get(current.getMcpPath());
        if (children != null && !children.isEmpty()) {
            Map<String, Object> props = new HashMap<>();
            List<String> reqs = new ArrayList<>();

            // 排序
            children.sort((o1, o2) -> {
                int s1 = o1.getSortOrder() != null ? o1.getSortOrder() : 0;
                int s2 = o2.getSortOrder() != null ? o2.getSortOrder() : 0;
                return Integer.compare(s1, s2);
            });

            for (McpToolProtocolConfigVO.ProtocolMapping child : children) {
                // 注意，buildProperty 嵌套递归，一层层的寻找，是否还有孩子元素（children）
                props.put(child.getFieldName(), buildProperty(child, childrenMap));
                if (Integer.valueOf(1).equals(child.getIsRequired())) {
                    reqs.add(child.getFieldName());
                }
            }

            property.put("properties", props);

            if (!reqs.isEmpty()) {
                property.put("required", reqs);
            }

        }

        return property;
    }

}
