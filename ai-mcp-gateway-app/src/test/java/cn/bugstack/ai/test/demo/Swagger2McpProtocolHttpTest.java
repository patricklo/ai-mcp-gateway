package cn.bugstack.ai.test.demo;

import cn.bugstack.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Swagger2McpProtocolHttpTest {

    @Value("classpath:swagger/api-docs-test03.json")
    private Resource apiDocs;

    @Test
    public void parseSwaggerAndBuildHTTPProtocolVO() throws Exception {
        String json = new String(FileCopyUtils.copyToByteArray(apiDocs.getInputStream()), StandardCharsets.UTF_8);
        List<String> endpoints = Arrays.asList("/api/v1/mcp/get_company_employee");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-test03");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-test02");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-by-id-01");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-by-id-02");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-by-id-03");
        List<HTTPProtocolVO> result = parse(json, endpoints);
        log.info("测试结果:{}", JSON.toJSONString(result));
    }

    private List<HTTPProtocolVO> parse(String json, List<String> endpoints) {
        JSONObject root = JSON.parseObject(json);

        String baseUrl = root.getJSONArray("servers").getJSONObject(0).getString("url");

        JSONObject paths = root.getJSONObject("paths");
        JSONObject schemas = root.getJSONObject("components").getJSONObject("schemas");

        List<HTTPProtocolVO> list = new ArrayList<>();

        for (String endpoint : endpoints) {
            JSONObject pathItem = paths.getJSONObject(endpoint);
            if (pathItem == null) continue;

            String method = detectMethod(pathItem);
            JSONObject operation = pathItem.getJSONObject(method);

            HTTPProtocolVO vo = new HTTPProtocolVO();
            vo.setHttpUrl(baseUrl + endpoint);
            vo.setHttpMethod(method);
            vo.setHttpHeaders(JSON.toJSONString(new HashMap<>() {{
                put("Content-Type", "application/json");
            }}));
            vo.setTimeout(30000);

            List<HTTPProtocolVO.ProtocolMapping> mappings = new ArrayList<>();

            // 1. Parse requestBody - 对象入参
            JSONObject requestBody = operation.getJSONObject("requestBody");
            if (requestBody != null) {
                JSONObject content = requestBody.getJSONObject("content");
                JSONObject appJson = content.getJSONObject("application/json");
                if (appJson != null) {
                    JSONObject schema = appJson.getJSONObject("schema");
                    String ref = schema.getString("$ref");

                    if (ref != null) {
                        String refName = ref.substring(ref.lastIndexOf('/') + 1);
                        JSONObject reqSchema = schemas.getJSONObject(refName);
                        String rootName = toLowerCamel(refName);

                        HTTPProtocolVO.ProtocolMapping rootMapping = HTTPProtocolVO.ProtocolMapping.builder()
                                .mappingType("request")
                                .parentPath(null)
                                .fieldName(rootName)
                                .mcpPath(rootName)
                                .mcpType(convertType(reqSchema.getString("type")))
                                .mcpDesc(reqSchema.getString("description"))
                                .isRequired(1)
                                .sortOrder(1)
                                .build();
                        mappings.add(rootMapping);

                        parseProperties(rootName, reqSchema.getJSONObject("properties"), reqSchema.getJSONArray("required"), schemas, mappings);
                    }
                }
            }

            // 2. Parse parameters - 属性入参
            JSONArray parameters = operation.getJSONArray("parameters");
            if (parameters != null) {
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
                        JSONObject reqSchema = schemas.getJSONObject(refName);
                        
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

                        parseProperties(name, reqSchema.getJSONObject("properties"), reqSchema.getJSONArray("required"), schemas, mappings);
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

            vo.setMappings(mappings);
            list.add(vo);
        }
        return list;
    }

    private void parseProperties(String parentMcpPath, JSONObject properties, JSONArray requiredList, JSONObject definitions, List<HTTPProtocolVO.ProtocolMapping> mappings) {
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

    // MCP数据类型：string/number/boolean/object/array
    private String convertType(String type) {
        if (type == null) return "string";
        return switch (type.toLowerCase()) {
            case "string", "char", "date", "datetime" -> "string";
            case "integer", "int", "long", "double", "float", "number" -> "number";
            case "boolean", "bool" -> "boolean";
            case "array", "list" -> "array";
            default -> "object";
        };
    }

    // 检测方法
    private String detectMethod(JSONObject pathItem) {
        if (pathItem.containsKey("post")) return "post";
        if (pathItem.containsKey("get")) return "get";
        if (pathItem.containsKey("put")) return "put";
        if (pathItem.containsKey("delete")) return "delete";
        return "post";
    }

    private String toLowerCamel(String name) {
        if (name == null || name.isEmpty()) return name;
        char[] cs = name.toCharArray();
        cs[0] = java.lang.Character.toLowerCase(cs[0]);
        return new String(cs);
    }

}
