package cn.bugstack.ai.trigger.http;

import cn.bugstack.ai.api.IMcpGatewayService;
import cn.bugstack.ai.api.response.Response;
import cn.bugstack.ai.cases.mcp.IMcpMessageService;
import cn.bugstack.ai.cases.mcp.IMcpSessionService;
import cn.bugstack.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.bugstack.ai.types.enums.ResponseCode;
import cn.bugstack.ai.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * MCP 网关服务接口管理
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 08:54
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/")
public class McpGatewayController implements IMcpGatewayService {

    @Resource(name = "mcpSSESessionService")
    private IMcpSessionService mcpSessionService;

    @Resource(name = "mcpSSEMessageService")
    private IMcpMessageService<Void> mcpMessageService;

    /**
     * 处理 sse 连接，创建会话
     * <br/>
     * <a href="http://localhost:8777/api-gateway/gateway_001/mcp/sse">http://localhost:8777/api-gateway/gateway_001/mcp/sse</a>
     * <br/>
     * <a href="http://localhost:8777/api-gateway/gateway_001/mcp/sse?api_key=gw-lf3HFzlJCdnrYl20oHbd5lJQxE7GWz8wjsSgjDZfctJNV8s5">http://localhost:8777/api-gateway/gateway_001/mcp/sse?api_key=gw-lf3HFzlJCdnrYl20oHbd5lJQxE7GWz8wjsSgjDZfctJNV8s5</a>
     *
     * @param gatewayId 网关ID
     */
    @GetMapping(value = "{gatewayId}/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Override
    public Flux<ServerSentEvent<String>> handleSseConnection(
            @PathVariable("gatewayId") String gatewayId, @RequestParam(value = "api_key", required = false, defaultValue = "") String apiKey) throws Exception {
        try {
            log.info("建立 MCP SSE 连接，gatewayId:{}", gatewayId);
            if (StringUtils.isBlank(gatewayId)) {
                log.info("非法参数，gateway is null");
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            return mcpSessionService.createMcpSession(gatewayId, apiKey);
        } catch (AppException e) {
            log.error("建立 MCP SSE 连接拒绝，gatewayId: {}", gatewayId, e);
            return Flux.just(ServerSentEvent.<String>builder()
                    .id(UUID.randomUUID().toString())
                    .event("error")
                    .data(JSON.toJSONString(Response.<String>builder()
                            .code(e.getCode())
                            .info(e.getInfo())
                            .build()))
                    .build());
        } catch (Exception e) {
            log.error("建立 MCP SSE 连接失败，gatewayId: {}", gatewayId, e);
            throw e;
        }
    }

    /**
     * 处理 sse 消息，响应会话
     *
     * @param gatewayId   网关ID
     * @param sessionId   会话ID
     * @param messageBody 请求消息
     * @return 响应结果
     * <br/>
     * {
     * "jsonrpc": "2.0",
     * "method": "initialize",
     * "id": "95835f74-0",
     * "params": {
     * "protocolVersion": "2024-11-05",
     * "capabilities": {},
     * "clientInfo": {
     * "name": "Java SDK MCP Client",
     * "version": "1.0.0"
     * }
     * }
     * }
     */
    @PostMapping(value = "{gatewayId}/mcp/sse", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> handleMessage(@PathVariable("gatewayId") String gatewayId,
                                                    @RequestParam("sessionId") String sessionId,
                                                    @RequestParam(value = "api_key", required = false, defaultValue = "") String apiKey,
                                                    @RequestBody String messageBody) {
        try {
            log.info("处理 MCP SSE 消息，gatewayId:{} apiKey:{} sessionId:{} messageBody:{}", gatewayId, apiKey, sessionId, messageBody);
            if (StringUtils.isBlank(gatewayId) || StringUtils.isBlank(sessionId)) {
                log.info("非法参数，gateway、sessionId is null");
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            HandleMessageCommandEntity commandEntity = new HandleMessageCommandEntity(gatewayId, apiKey, sessionId, messageBody);
            ResponseEntity<Void> responseEntity = mcpMessageService.handleMessage(commandEntity);

            return Mono.just(responseEntity);
        } catch (Exception e) {
            log.error("处理 MCP SSE 消息失败，gatewayId:{} sessionId:{} messageBody:{}", gatewayId, sessionId, messageBody, e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }
    }

}
