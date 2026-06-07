package cn.bugstack.ai.trigger.http;

import cn.bugstack.ai.api.IMcpGatewayService;
import cn.bugstack.ai.cases.mcp.IMcpSessionService;
import cn.bugstack.ai.domain.session.model.valobj.McpSchemaVO;
import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;
import cn.bugstack.ai.domain.session.service.ISessionManagementService;
import cn.bugstack.ai.domain.session.service.ISessionMessageService;
import cn.bugstack.ai.types.enums.ResponseCode;
import cn.bugstack.ai.types.exception.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

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

    @Resource
    private IMcpSessionService mcpSessionService;

    // todo 暂时调用 domain 测试，后续调用 case 编排
    @Resource
    private ISessionMessageService serviceMessageService;

    @Resource
    private ISessionManagementService sessionManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 处理 sse 连接，创建会话
     * <br/>
     * <a href="http://localhost:8777/api-gateway/test10001/mcp/sse">http://localhost:8777/api-gateway/test10001/mcp/sse</a>
     *
     * @param gatewayId 网关ID
     */
    @GetMapping(value = "{gatewayId}/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Override
    public Flux<ServerSentEvent<String>> handleSseConnection(@PathVariable("gatewayId") String gatewayId) throws Exception {
        try {
            log.info("建立 MCP SSE 连接，gatewayId:{}", gatewayId);
            if (StringUtils.isBlank(gatewayId)) {
                log.info("非法参数，gateway is null");
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            return mcpSessionService.createMcpSession(gatewayId);
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
                                                    @RequestBody String messageBody) {
        try {
            log.info("处理 MCP SSE 消息，gatewayId:{} sessionId:{} messageBody:{}", gatewayId, sessionId, messageBody);

            SessionConfigVO session = sessionManagementService.getSession(sessionId);
            if (null == session) {
                log.warn("会话不存在或已过期，gatewayId:{} sessionId:{}", gatewayId, sessionId);
                return Mono.just(ResponseEntity.notFound().build());
            }

            McpSchemaVO.JSONRPCMessage jsonrpcMessage = McpSchemaVO.deserializeJsonRpcMessage(messageBody);
            log.info("序列化消息:{}", jsonrpcMessage.jsonrpc());

            // 暂时直接调用 domain，后续调整
            McpSchemaVO.JSONRPCResponse jsonrpcResponse = serviceMessageService.processHandlerMessage(gatewayId, jsonrpcMessage);
            if (null != jsonrpcResponse) {
                String responseJson = objectMapper.writeValueAsString(jsonrpcResponse);
                session.getSink().tryEmitNext(ServerSentEvent.<String>builder()
                        .event("message")
                        .data(responseJson)
                        .build());
            }

            return Mono.just(ResponseEntity.accepted().build());
        } catch (Exception e) {
            log.error("处理 MCP SSE 消息失败，gatewayId:{} sessionId:{} messageBody:{}", gatewayId, sessionId, messageBody, e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }
    }

}
