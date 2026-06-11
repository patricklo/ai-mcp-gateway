package cn.bugstack.ai.domain.protocol.service.analysis;

import cn.bugstack.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.bugstack.ai.domain.protocol.model.valobj.enums.AnalysisTypeEnum;
import cn.bugstack.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.bugstack.ai.domain.protocol.service.IProtocolAnalysis;
import cn.bugstack.ai.domain.protocol.service.analysis.strategy.IProtocolAnalysisStrategy;
import cn.bugstack.ai.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协议解析服务
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/3 07:30
 */
@Slf4j
@Service
public class ProtocolAnalysis implements IProtocolAnalysis {

    private final Map<String, IProtocolAnalysisStrategy> protocolAnalysisStrategyMap;

    public ProtocolAnalysis(Map<String, IProtocolAnalysisStrategy> protocolAnalysisStrategyMap) {
        this.protocolAnalysisStrategyMap = protocolAnalysisStrategyMap;
    }

    @Override
    public List<HTTPProtocolVO> doAnalysis(AnalysisCommandEntity commandEntity) {
        log.info("协议解析请求 endpoints:{} openApiJson:{}", JSON.toJSONString(commandEntity.getEndpoints()), commandEntity.getOpenApiJson());

        List<HTTPProtocolVO> list = new ArrayList<>();
        try {
            JSONObject root = JSON.parseObject(commandEntity.getOpenApiJson());
            String baseUrl = root.getJSONArray("servers").getJSONObject(0).getString("url");
            JSONObject paths = root.getJSONObject("paths");
            JSONObject schemas = root.getJSONObject("components").getJSONObject("schemas");

            List<String> endpoints = commandEntity.getEndpoints();
            if (null == endpoints || endpoints.isEmpty()) return list;

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

                // 枚举策略动作处理
                AnalysisTypeEnum.SwaggerAnalysisAction analysisAction = AnalysisTypeEnum.SwaggerAnalysisAction.get(operation);
                IProtocolAnalysisStrategy strategy = protocolAnalysisStrategyMap.get(analysisAction.getCode());
                strategy.doAnalysis(operation, schemas, mappings);

                vo.setMappings(mappings);
                list.add(vo);
            }

        } catch (Exception e) {
            log.error("协议解析失败 endpoints:{} openApiJson:{}", JSON.toJSONString(commandEntity.getEndpoints()), commandEntity.getOpenApiJson(), e);
        }

        return list;
    }

    private String detectMethod(JSONObject pathItem) {
        if (pathItem.containsKey("post")) return "post";
        if (pathItem.containsKey("get")) return "get";
        if (pathItem.containsKey("put")) return "put";
        if (pathItem.containsKey("delete")) return "delete";
        return "post";
    }

}
