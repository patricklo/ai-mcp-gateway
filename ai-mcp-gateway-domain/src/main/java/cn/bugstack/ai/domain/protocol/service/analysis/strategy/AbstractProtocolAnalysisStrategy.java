package cn.bugstack.ai.domain.protocol.service.analysis.strategy;

import cn.bugstack.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 协议解析策略抽象类
 *
 * @author xiaofuge bugstack.cn @小傅哥
 */
public abstract class AbstractProtocolAnalysisStrategy implements IProtocolAnalysisStrategy {

    protected void parseProperties(String parentMcpPath, JSONObject properties, JSONArray requiredList, JSONObject definitions, List<HTTPProtocolVO.ProtocolMapping> mappings) {
        if (properties == null) return;

        int sortOrder = 1;

        for (String propName : properties.keySet()) {
            JSONObject prop = properties.getJSONObject(propName);

            // 拼接关系链 a.b.c
            String currentMcpPath = parentMcpPath + "." + propName;

            JSONObject effectiveSchema = prop;
            String type = prop.getString("type");
            String description = prop.getString("description");

            // 获取应用关系对象
            if (prop.containsKey("$ref")) {
                String ref = prop.getString("$ref");
                String refName = ref.substring(ref.lastIndexOf('/') + 1);
                effectiveSchema = definitions.getJSONObject(refName);
                if (type == null) type = effectiveSchema.getString("type");
                if (description == null) description = effectiveSchema.getString("description");
            }

            HTTPProtocolVO.ProtocolMapping mapping = HTTPProtocolVO.ProtocolMapping.builder()
                    .mappingType("request")
                    .parentPath(parentMcpPath)
                    .fieldName(propName)
                    .mcpPath(currentMcpPath)
                    .mcpType(convertType(type))
                    .mcpDesc(description)
                    .isRequired(requiredList != null && requiredList.contains(propName) ? 1 : 0)
                    .sortOrder(sortOrder++)
                    .build();
            mappings.add(mapping);

            // 如果存在下一个引用对象，则嵌套循环继续寻找
            if (effectiveSchema.containsKey("properties")) {
                parseProperties(currentMcpPath, effectiveSchema.getJSONObject("properties"), effectiveSchema.getJSONArray("required"), definitions, mappings);
            }
        }
    }

    protected String convertType(String type) {
        if (type == null) return "string";
        return switch (type.toLowerCase()) {
            case "string", "char", "date", "datetime" -> "string";
            case "integer", "int", "long", "double", "float", "number" -> "number";
            case "boolean", "bool" -> "boolean";
            case "array", "list" -> "array";
            default -> "object";
        };
    }

    protected String toLowerCamel(String name) {
        if (name == null || name.isEmpty()) return name;
        char[] cs = name.toCharArray();
        cs[0] = java.lang.Character.toLowerCase(cs[0]);
        return new String(cs);
    }

}
