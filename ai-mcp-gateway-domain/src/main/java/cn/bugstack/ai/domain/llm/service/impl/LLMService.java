package cn.bugstack.ai.domain.llm.service.impl;

import cn.bugstack.ai.domain.llm.model.entity.BuildChatModelCommandEntity;
import cn.bugstack.ai.domain.llm.model.valobj.McpConfigVO;
import cn.bugstack.ai.domain.llm.service.ILLMService;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
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


@Slf4j
@Service
public class LLMService implements ILLMService {

    private final Map<String, ChatModel> chatModelMap = new HashMap<>();

    @Resource
    private OpenAiApi openAiApi;

    @Value("${spring.ai.openai.options.model:gpt-4o}")
    private String model;

    @Override
    public void buildChatModel(BuildChatModelCommandEntity commandEntity) {
        log.info("build chat model gatewayid:{} mcp:{}", commandEntity.getGatewayId(), commandEntity.getMcpConfigVO());

        McpConfigVO mcpConfigVO = commandEntity.getMcpConfigVO();

        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .toolCallbacks(buildToolCallback(mcpConfigVO))
                        .build())
                .build();

        chatModelMap.put(commandEntity.getGatewayId(), chatModel);

    }

    public ToolCallback[] buildToolCallback(McpConfigVO mcpConfigVO) {
        String sseEndPoint = mcpConfigVO.getSseEndpoint();
        if(StringUtils.isNotBlank(mcpConfigVO.getAuthApiKey())) {
            sseEndPoint += "?api_key="+mcpConfigVO.getAuthApiKey();
        }

        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                .builder(mcpConfigVO.getBaseUrl())
                .sseEndpoint(sseEndPoint)
                .build();

        McpSyncClient mcpSyncClient = McpClient
                .sync(sseClientTransport)
                .requestTimeout(Duration.ofMillis(mcpConfigVO.getTimeout()))
                .build();

        var initilize = mcpSyncClient.initialize();

        log.info("tool sse mcp initilize {}", initilize);

        return new SyncMcpToolCallbackProvider(mcpSyncClient).getToolCallbacks();
    }

    @Override
    public ChatModel getChatModel(String gatewayId) {
        return null;
    }
}
