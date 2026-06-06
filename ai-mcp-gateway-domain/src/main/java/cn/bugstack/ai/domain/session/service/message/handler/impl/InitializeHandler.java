package cn.bugstack.ai.domain.session.service.message.handler.impl;

import cn.bugstack.ai.domain.session.adapter.repository.ISessionRepository;
import cn.bugstack.ai.domain.session.model.valobj.McpSchemaVO;
import cn.bugstack.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.bugstack.ai.domain.session.service.message.handler.IRequestHandler;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * 协议握手，建立客户端与服务器的连接
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/20 11:28
 */
@Slf4j
@Service("initializeHandler")
public class InitializeHandler implements IRequestHandler {

    @Resource
    private ISessionRepository repository;

    /**
     * 对照 io.modelcontextprotocol.spec.McpServerSession
     * <br/>
     * McpServerSession.handle -> McpSchema.JSONRPCRequest -> handleIncomingRequest
     * -> McpSchema.METHOD_INITIALIZE -> McpAsyncServer.asyncInitializeRequestHandler
     * -> result -> new McpSchema.JSONRPCResponse(McpSchema.JSONRPC_VERSION, request.id(), result, null)
     * <br/>
     * {
     * "id": "a355a5f7-0",
     * "jsonrpc": "2.0",
     * "result": {
     * "capabilities": {
     * "completions": {},
     * "logging": {},
     * "prompts": {
     * "listChanged": true
     * },
     * "resources": {
     * "listChanged": true,
     * "subscribe": false
     * },
     * "tools": {
     * "listChanged": true
     * }
     * },
     * "instructions": "This server provides weather information tools and resources",
     * "protocolVersion": "2024-11-05",
     * "serverInfo": {
     * "name": "ai-mcp-gateway-demo-mcp-server-test",
     * "version": "1.0.0"
     * }
     * }
     * }
     */
    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {
        log.info("消息处理服务-initialize gatewayId:{} request.params:{}", gatewayId, JSON.toJSONString(message.params()));

        // 1. 转换参数
        McpSchemaVO.InitializeRequest initializeRequest = McpSchemaVO.unmarshalFrom(message.params(), new TypeReference<>() {
        });

        // 2. 查询配置 todo 后续拆分，这部分只查询网关配置，不使用工具配置信息
        McpGatewayConfigVO mcpGatewayConfigVO = repository.queryMcpGatewayConfigByGatewayId(gatewayId);

        // 3. 组装信息
        McpSchemaVO.InitializeResult initializeResult = new McpSchemaVO.InitializeResult(initializeRequest.protocolVersion(),
                new McpSchemaVO.ServerCapabilities(new McpSchemaVO.ServerCapabilities.CompletionCapabilities(),
                        new HashMap<>(),
                        new McpSchemaVO.ServerCapabilities.LoggingCapabilities(),
                        new McpSchemaVO.ServerCapabilities.PromptCapabilities(true),
                        new McpSchemaVO.ServerCapabilities.ResourceCapabilities(false, true),
                        new McpSchemaVO.ServerCapabilities.ToolCapabilities(true)),
                new McpSchemaVO.Implementation(mcpGatewayConfigVO.getToolName(), mcpGatewayConfigVO.getToolVersion()),
                mcpGatewayConfigVO.getToolDesc()
        );

        // 4. 返回结果
        return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), initializeResult, null);
    }

}
