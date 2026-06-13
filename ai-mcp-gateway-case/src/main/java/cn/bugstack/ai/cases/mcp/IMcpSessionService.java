package cn.bugstack.ai.cases.mcp;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * 会话服务策略接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 09:07
 */
public interface IMcpSessionService {

    /**
     * 创建 MCP 会话服务
     *
     * @param gatewayId 网关ID（后续还要扩展 apiKey 验证字段）
     * @return 流式响应
     */
    Flux<ServerSentEvent<String>> createMcpSession(String gatewayId, String apiKey) throws Exception;

    /**
     * 获取 MCP 会话服务
     *
     * @param sessionId 会话ID
     * @return 流式响应
     */
    Flux<ServerSentEvent<String>> getMcpSession(String gatewayId, String apiKey, String sessionId) throws Exception;

    /**
     * 删除 MCP 会话服务
     * @param sessionId – 会话ID
     */
    void deleteMcpSession(String sessionId);

}
