package cn.bugstack.ai.infrastructure.adapter.repository;

import cn.bugstack.ai.domain.session.adapter.repository.ISessionRepository;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpGatewayProtocolConfigVO;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpGatewayToolConfigVO;
import cn.bugstack.ai.infrastructure.dao.IMcpGatewayDao;
import cn.bugstack.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.bugstack.ai.infrastructure.dao.IMcpProtocolRegistryDao;
import cn.bugstack.ai.infrastructure.dao.po.McpGatewayPO;
import cn.bugstack.ai.infrastructure.dao.po.McpProtocolMappingPO;
import cn.bugstack.ai.infrastructure.dao.po.McpProtocolRegistryPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 会话仓储服务
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/1/13 08:25
 */
@Slf4j
@Repository
public class SessionRepository implements ISessionRepository {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Resource
    private IMcpProtocolRegistryDao mcpProtocolRegistryDao;

    @Resource
    private IMcpProtocolMappingDao mcpProtocolMappingDao;

    @Override
    public McpGatewayConfigVO queryMcpGatewayConfigByGatewayId(String gatewayId) {
        // 1. 查询网关配置（这里只判空，返回null就可以）
        McpGatewayPO mcpGatewayPO = mcpGatewayDao.queryMcpGatewayByGatewayId(gatewayId);
        if (null == mcpGatewayPO) return null;

        // 2. 查询协议注册（1:1 -> gatewayId:toolId）
        McpProtocolRegistryPO mcpProtocolRegistryPO = mcpProtocolRegistryDao.queryMcpProtocolRegistryByGatewayId(gatewayId);
        if (null == mcpProtocolRegistryPO) return null;

        return McpGatewayConfigVO.builder()
                .gatewayId(mcpGatewayPO.getGatewayId())
                .gatewayName(mcpGatewayPO.getGatewayName())
                .toolId(mcpProtocolRegistryPO.getToolId())
                .toolName(mcpProtocolRegistryPO.getToolName())
                .toolDesc(mcpProtocolRegistryPO.getToolDescription())
                .toolVersion(mcpProtocolRegistryPO.getToolVersion())
                .build();
    }

    @Override
    public List<McpGatewayToolConfigVO> queryMcpGatewayToolConfigListByGatewayId(String gatewayId) {
        McpProtocolMappingPO reqPO = new McpProtocolMappingPO();
        reqPO.setGatewayId(gatewayId);

        // 1. 查询协议工具映射配置
        List<McpProtocolMappingPO> poList = mcpProtocolMappingDao.queryMcpGatewayToolConfigList(reqPO);

        // 2. 转换为领域值对象返回
        List<McpGatewayToolConfigVO> mcpGatewayToolConfigVOS = new ArrayList<>();
        for (McpProtocolMappingPO po : poList) {
            mcpGatewayToolConfigVOS.add(McpGatewayToolConfigVO.builder()
                        .gatewayId(po.getGatewayId())
                        .toolId(po.getToolId())
                        .mappingType(po.getMappingType())
                        .parentPath(po.getParentPath())
                        .fieldName(po.getFieldName())
                        .mcpPath(po.getMcpPath())
                        .mcpType(po.getMcpType())
                        .mcpDesc(po.getMcpDesc())
                        .isRequired(po.getIsRequired())
                        .sortOrder(po.getSortOrder())
                        .build());
        }

        return mcpGatewayToolConfigVOS;
    }

    @Override
    public McpGatewayProtocolConfigVO queryMcpGatewayProtocolConfig(String gatewayId) {

        McpProtocolRegistryPO mcpProtocolRegistryPO = mcpProtocolRegistryDao.queryMcpProtocolRegistryByGatewayId(gatewayId);
        if (null == mcpProtocolRegistryPO) return null;

        McpGatewayProtocolConfigVO.HTTPConfig httpConfig = new McpGatewayProtocolConfigVO.HTTPConfig();
        httpConfig.setHttpUrl(mcpProtocolRegistryPO.getHttpUrl());
        httpConfig.setHttpHeaders(mcpProtocolRegistryPO.getHttpHeaders());
        httpConfig.setHttpMethod(mcpProtocolRegistryPO.getHttpMethod());
        httpConfig.setTimeout(mcpProtocolRegistryPO.getTimeout());

        return McpGatewayProtocolConfigVO.builder().httpConfig(httpConfig).build();
    }

}
