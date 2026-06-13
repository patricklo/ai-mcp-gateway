package cn.bugstack.ai.domain.llm.service.impl;

import cn.bugstack.ai.domain.llm.model.entity.BuildChatModelCommandEntity;
import cn.bugstack.ai.domain.llm.model.valobj.McpConfigVO;
import cn.bugstack.ai.domain.llm.model.valobj.McpTypeEnumVO;
import cn.bugstack.ai.domain.llm.service.ILLMService;
import com.alibaba.fastjson.JSON;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 大模型服务
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/4/8 07:03
 */
@Slf4j
@Service
public class LLMService implements ILLMService {

    private final Map<String, ChatModel> chatModelMap = new HashMap<>();

    private final Map<McpTypeEnumVO, ToolCallbackBuilderStrategy> strategyMap = new HashMap<>();

    @Resource
    private OpenAiApi openAiApi;

    @Value("${spring.ai.openai.options.model:gpt-4o}")
    private String model;

    public LLMService() {
        strategyMap.put(McpTypeEnumVO.SSE, new SseToolCallbackBuilderStrategy());
        strategyMap.put(McpTypeEnumVO.STREAMABLE, new StreamableToolCallbackBuilderStrategy());
    }

    @Override
    public void buildChatModel(BuildChatModelCommandEntity commandEntity) {
        log.info("构建对话模型 gatewayId:{} mcp:{}, type:{}", commandEntity.getGatewayId(), JSON.toJSONString(commandEntity.getMcpConfigVO()), commandEntity.getMcpType());

        // mcp 配置
        McpConfigVO mcpConfigVO = commandEntity.getMcpConfigVO();

        // model 配置 + mcp 服务
        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .toolCallbacks(buildToolCallback(mcpConfigVO, commandEntity.getMcpType()))
                        .build())
                .build();

        // 写入缓存
        chatModelMap.put(commandEntity.getGatewayId(), chatModel);
    }

    private interface ToolCallbackBuilderStrategy {
        ToolCallback[] build(McpConfigVO config);
    }

    private static class SseToolCallbackBuilderStrategy implements ToolCallbackBuilderStrategy {

        @Override
        public ToolCallback[] build(McpConfigVO mcpConfigVO) {
            String sseEndPoint = mcpConfigVO.getSseEndpoint();
            if (StringUtils.isNotBlank(mcpConfigVO.getAuthApiKey())) {
                sseEndPoint += "?api_key=" + mcpConfigVO.getAuthApiKey();
            }

            HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                    .builder(mcpConfigVO.getBaseUri())
                    .sseEndpoint(sseEndPoint)
                    .build();

            McpSyncClient mcpSyncClient = McpClient
                    .sync(sseClientTransport)
                    .requestTimeout(Duration.ofMillis(mcpConfigVO.getTimeout())).build();
            var initialize = mcpSyncClient.initialize();
            log.info("tool sse mcp initialize:{]", initialize);
            return SyncMcpToolCallbackProvider.builder().mcpClients(mcpSyncClient).build().getToolCallbacks();
        }
    }

    /**
     * Streamable 策略实现
     */
    private static class StreamableToolCallbackBuilderStrategy implements ToolCallbackBuilderStrategy {
        @Override
        public ToolCallback[] build(McpConfigVO mcpConfigVO) {
            McpClientTransport mcpClientTransport = HttpClientStreamableHttpTransport
                    .builder(mcpConfigVO.getBaseUri())
                    .endpoint(mcpConfigVO.getSseEndpoint())
                    .build();
            McpSyncClient mcpSyncClient = McpClient.sync(mcpClientTransport)
                    .requestTimeout(Duration.ofMillis(mcpConfigVO.getTimeout())).build();
            var init_streamable = mcpSyncClient.initialize();
            log.info("tool streamable mcp initialize {}", init_streamable);
            return SyncMcpToolCallbackProvider.builder().mcpClients(mcpSyncClient).build().getToolCallbacks();
        }
    }

    public ToolCallback[] buildToolCallback(McpConfigVO mcpConfigVO, McpTypeEnumVO mcpType) {
        ToolCallbackBuilderStrategy strategy = strategyMap.get(mcpType);
        if (strategy == null) {
            // 默认 SSE
            strategy = strategyMap.get(McpTypeEnumVO.SSE);
        }
        return strategy.build(mcpConfigVO);
    }

    @Override
    public ChatModel getChatModel(String gatewayId) {
        return chatModelMap.get(gatewayId);
    }

}
