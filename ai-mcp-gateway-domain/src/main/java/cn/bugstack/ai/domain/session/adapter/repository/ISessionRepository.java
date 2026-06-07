package cn.bugstack.ai.domain.session.adapter.repository;

import cn.bugstack.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpToolConfigVO;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpToolProtocolConfigVO;

import java.util.List;

/**
 * 会话仓储接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/1/13 07:49
 */
public interface ISessionRepository {

    McpGatewayConfigVO queryMcpGatewayConfigByGatewayId(String gatewayId);

    List<McpToolConfigVO> queryMcpGatewayToolConfigListByGatewayId(String gatewayId);

    McpToolProtocolConfigVO queryMcpGatewayProtocolConfig(String gatewayId, String toolName);

}
