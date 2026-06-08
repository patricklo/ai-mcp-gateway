package cn.bugstack.ai.domain.session.service;

import cn.bugstack.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.bugstack.ai.domain.session.model.valobj.McpSchemaVO;

/**
 * 会话消息服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/20 08:49
 */
public interface ISessionMessageService {

    McpSchemaVO.JSONRPCResponse processHandlerMessage(String gatewayId, McpSchemaVO.JSONRPCMessage message);

    McpSchemaVO.JSONRPCResponse processHandlerMessage(HandleMessageCommandEntity commandEntity);

}
