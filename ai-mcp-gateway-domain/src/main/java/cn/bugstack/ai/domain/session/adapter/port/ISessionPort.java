package cn.bugstack.ai.domain.session.adapter.port;

import cn.bugstack.ai.domain.session.model.valobj.gateway.McpGatewayProtocolConfigVO;

import java.io.IOException;

/**
 * 回话端口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/1/30 20:55
 */
public interface ISessionPort {

    Object toolCall(McpGatewayProtocolConfigVO.HTTPConfig httpConfig, Object params) throws IOException;

}
