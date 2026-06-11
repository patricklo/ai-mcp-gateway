package cn.bugstack.ai.domain.protocol.service.analysis.strategy.impl;

import cn.bugstack.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.bugstack.ai.domain.protocol.service.analysis.strategy.AbstractProtocolAnalysisStrategy;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * parameters 解析策略
 *
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@Component("parametersAnalysis")
@Order(2)
public class ParametersAnalysisStrategy extends AbstractProtocolAnalysisStrategy {

    @Override
    public void doAnalysis(JSONObject operation, JSONObject definitions, List<HTTPProtocolVO.ProtocolMapping> mappings) {
        JSONArray parameters = operation.getJSONArray("parameters");
        if (parameters == null) return;

        for (int i = 0; i < parameters.size(); i++) {
            JSONObject param = parameters.getJSONObject(i);
            String in = param.getString("in");
            if (!"query".equals(in) && !"path".equals(in)) continue;

            String name = param.getString("name");
            boolean required = param.getBooleanValue("required");
            String description = param.getString("description");

            JSONObject schema = param.getJSONObject("schema");
            String type = schema.getString("type");
            String ref = schema.getString("$ref");

            if (ref != null) {
                String refName = ref.substring(ref.lastIndexOf('/') + 1);
                JSONObject reqSchema = definitions.getJSONObject(refName);

                if (type == null) type = reqSchema.getString("type");
                if (description == null) description = reqSchema.getString("description");

                HTTPProtocolVO.ProtocolMapping rootMapping = HTTPProtocolVO.ProtocolMapping.builder()
                        .mappingType("request")
                        .parentPath(null)
                        .fieldName(name)
                        .mcpPath(name)
                        .mcpType(convertType(type))
                        .mcpDesc(description)
                        .isRequired(required ? 1 : 0)
                        .sortOrder(mappings.size() + 1)
                        .build();

                mappings.add(rootMapping);

                parseProperties(name, reqSchema.getJSONObject("properties"), reqSchema.getJSONArray("required"), definitions, mappings);
            } else {
                HTTPProtocolVO.ProtocolMapping mapping = HTTPProtocolVO.ProtocolMapping.builder()
                        .mappingType("request")
                        .parentPath(null)
                        .fieldName(name)
                        .mcpPath(name)
                        .mcpType(convertType(type))
                        .mcpDesc(description)
                        .isRequired(required ? 1 : 0)
                        .sortOrder(mappings.size() + 1)
                        .build();
                mappings.add(mapping);
            }
        }
    }

}
