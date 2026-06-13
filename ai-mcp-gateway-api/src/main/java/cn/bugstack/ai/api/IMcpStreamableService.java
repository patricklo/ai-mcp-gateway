package cn.bugstack.ai.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MCP Streamable 接口
 */
public interface IMcpStreamableService {

    /**
     * 建立 Streamable SSE 监听连接，要求客户端携带 Mcp-Session-Id，兼容 query sessionId
     */
    Flux<ServerSentEvent<String>> handleGet(String gatewayId, String apiKey, String paramSessionId, String headerSessionId, HttpHeaders headers);

    /**
     * 接收 MCP Streamable 请求并返回 JSON RPC 响应或 202 Accepted，优先使用 Mcp-Session-Id，兼容 query sessionId
     */
    Mono<ResponseEntity<?>> handlePost(String gatewayId, String apiKey, String paramSessionId, String headerSessionId, String messageBody, HttpHeaders headers);

    /**
     * 接收 DELETE 请求，关闭会话，优先使用 Mcp-Session-Id，兼容 query sessionId
     */
    Mono<ResponseEntity<Void>> handleDelete(String gatewayId, String paramSessionId, String headerSessionId, HttpHeaders headers);

}
