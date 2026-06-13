package cn.bugstack.ai.trigger.http;

import cn.bugstack.ai.api.IMcpStreamableService;
import cn.bugstack.ai.cases.mcp.IMcpMessageService;
import cn.bugstack.ai.cases.mcp.IMcpSessionService;
import cn.bugstack.ai.cases.mcp.streamable.session.McpStreamableSessionService;
import cn.bugstack.ai.domain.session.model.entity.HandleMessageCommandEntity;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MCP Streamable 控制器
 * <p>
 * Trigger 层仅负责 HTTP 协议适配，真实会话生命周期与 MCP 消息处理下沉到 Case/Domain 层。
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/{gatewayId}/mcp")
public class McpStreamableController implements IMcpStreamableService {

    @Resource(name = "mcpStreamableSessionService")
    private IMcpSessionService mcpStreamableSessionService;

    @Resource(name = "mcpStreamableMessageService")
    private IMcpMessageService<String> mcpStreamableMessageService;

    /**
     * 提取请求头或认证信息作为传输上下文
     */
    private Map<String, String> extractContext(HttpHeaders headers) {
        Map<String, String> context = new HashMap<>();
        if (headers != null) {
            headers.forEach((key, value) -> context.put(key, String.join(",", value)));
        }
        return context;
    }

    @Override
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> handleGet(@PathVariable("gatewayId") String gatewayId,
                                                    @RequestParam(value = "api_key", required = false, defaultValue = "") String apiKey,
                                                    @RequestParam(value = "sessionId", required = false) String paramSessionId,
                                                    @RequestHeader(value = "Mcp-Session-Id", required = false) String headerSessionId,
                                                    @RequestHeader HttpHeaders headers) {
        String sessionId = StringUtils.isNotBlank(headerSessionId) ? headerSessionId : paramSessionId;
        Map<String, String> transportContext = extractContext(headers);
        log.info("MCP Streamable GET 监听连接，gatewayId:{} sessionId:{} context:{}", gatewayId, sessionId, transportContext);

        if (StringUtils.isBlank(gatewayId) || StringUtils.isBlank(sessionId)) {
            log.warn("MCP Streamable GET 参数非法，gatewayId:{} sessionId:{}", gatewayId, sessionId);
            return Flux.just(ServerSentEvent.<String>builder()
                    .id(UUID.randomUUID().toString())
                    .event("error")
                    .data("{\"error\": \"Invalid or missing gatewayId/sessionId\"}")
                    .build());
        }

        try {
            return mcpStreamableSessionService.getMcpSession(gatewayId, apiKey, sessionId)
                    .contextWrite(ctx -> ctx.put("MCP_TRANSPORT_CONTEXT", transportContext));
        } catch (Exception e) {
            log.error("MCP Streamable GET 监听连接失败，gatewayId:{} sessionId:{}", gatewayId, sessionId, e);
            return Flux.just(ServerSentEvent.<String>builder()
                    .id(UUID.randomUUID().toString())
                    .event("error")
                    .data(JSON.toJSONString(Map.of("error", e.getMessage())))
                    .build());
        }
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> handlePost(@PathVariable("gatewayId") String gatewayId,
                                               @RequestParam(value = "api_key", required = false, defaultValue = "") String apiKey,
                                               @RequestParam(value = "sessionId", required = false) String paramSessionId,
                                               @RequestHeader(value = "Mcp-Session-Id", required = false) String headerSessionId,
                                               @RequestBody String messageBody,
                                               @RequestHeader HttpHeaders headers) {
        String sessionId = StringUtils.isNotBlank(headerSessionId) ? headerSessionId : paramSessionId;
        Map<String, String> transportContext = extractContext(headers);
        log.info("MCP Streamable POST 收到消息，gatewayId:{} apiKey:{} sessionId:{} context:{} message:{}", gatewayId, apiKey, sessionId, transportContext, messageBody);

        try {
            if (StringUtils.isBlank(gatewayId) || StringUtils.isBlank(messageBody)) {
                log.warn("MCP Streamable POST 参数非法，gatewayId:{} messageBody:{}", gatewayId, messageBody);
                return Mono.just(ResponseEntity.badRequest().body("{\"error\": \"Invalid or missing gatewayId/messageBody\"}"));
            }

            HandleMessageCommandEntity commandEntity = new HandleMessageCommandEntity(gatewayId, apiKey, sessionId, messageBody);
            ResponseEntity<?> responseEntity = mcpStreamableMessageService.handleMessage(commandEntity);

            return Mono.<ResponseEntity<?>>just(responseEntity)
                    .contextWrite(ctx -> ctx.put("MCP_TRANSPORT_CONTEXT", transportContext));
        } catch (Exception e) {
            log.error("MCP Streamable POST 处理消息失败，gatewayId:{} sessionId:{} messageBody:{}", gatewayId, sessionId, messageBody, e);
            return Mono.just(ResponseEntity.internalServerError().body(JSON.toJSONString(Map.of("error", e.getMessage()))));
        }
    }

    @Override
    @DeleteMapping
    public Mono<ResponseEntity<Void>> handleDelete(@PathVariable("gatewayId") String gatewayId,
                                                   @RequestParam(value = "sessionId", required = false) String paramSessionId,
                                                   @RequestHeader(value = "Mcp-Session-Id", required = false) String headerSessionId,
                                                   @RequestHeader HttpHeaders headers) {
        String sessionId = StringUtils.isNotBlank(headerSessionId) ? headerSessionId : paramSessionId;
        Map<String, String> transportContext = extractContext(headers);
        log.info("MCP Streamable DELETE 关闭会话，gatewayId:{} sessionId:{} context:{}", gatewayId, sessionId, transportContext);

        if (StringUtils.isBlank(gatewayId) || StringUtils.isBlank(sessionId)) {
            log.warn("MCP Streamable DELETE 参数非法，gatewayId:{} sessionId:{}", gatewayId, sessionId);
            return Mono.just(ResponseEntity.badRequest().build());
        }

        mcpStreamableSessionService.deleteMcpSession(sessionId);
        return Mono.just(ResponseEntity.ok().<Void>build())
                .contextWrite(ctx -> ctx.put("MCP_TRANSPORT_CONTEXT", transportContext));
    }

}
