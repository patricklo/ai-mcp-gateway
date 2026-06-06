package cn.bugstack.ai.domain.session.service.message.handler;

import cn.bugstack.ai.domain.session.model.valobj.McpSchemaVO;

/**
 * 处理请求接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/20 09:09
 */
public interface IRequestHandler {

    McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message);

}
