package cn.bugstack.ai.domain.session.model.entity;

import cn.bugstack.ai.domain.session.model.valobj.McpSchemaVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 处理消息命令实体对象
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/20 07:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HandleMessageCommandEntity {

    private String gatewayId;

    private String apiKey;

    private String sessionId;

    private McpSchemaVO.JSONRPCMessage jsonrpcMessage;

    public HandleMessageCommandEntity(String gatewayId, String sessionId, String messageBody) throws Exception {
        this.gatewayId = gatewayId;
        this.sessionId = sessionId;
        this.jsonrpcMessage = McpSchemaVO.deserializeJsonRpcMessage(messageBody);
    }

    public HandleMessageCommandEntity(String gatewayId, String apiKey, String sessionId, String messageBody) throws Exception {
        this.gatewayId = gatewayId;
        this.apiKey = apiKey;
        this.sessionId = sessionId;
        this.jsonrpcMessage = McpSchemaVO.deserializeJsonRpcMessage(messageBody);
    }

}
