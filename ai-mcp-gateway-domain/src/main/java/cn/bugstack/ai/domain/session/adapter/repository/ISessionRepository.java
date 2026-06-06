package cn.bugstack.ai.domain.session.adapter.repository;

import cn.bugstack.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpGatewayToolConfigVO;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpGatewayProtocolConfigVO;

import java.util.List;

/**
 * 会话仓储接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/1/13 07:49
 */
public interface ISessionRepository {

    McpGatewayConfigVO queryMcpGatewayConfigByGatewayId(String gatewayId);

    List<McpGatewayToolConfigVO> queryMcpGatewayToolConfigListByGatewayId(String gatewayId);

    McpGatewayProtocolConfigVO queryMcpGatewayProtocolConfig(String gatewayId);

}
