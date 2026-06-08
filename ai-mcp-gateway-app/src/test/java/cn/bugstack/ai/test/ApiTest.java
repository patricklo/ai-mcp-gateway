package cn.bugstack.ai.test;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @Test
    public void test_mcp() {
        ChatClient chatClient = chatClientBuilder.defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gpt-4.1-mini")
                                .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient02()).getToolCallbacks())
                                .build())
                .build();

        // 有哪些工具可以使用
        log.info("测试结果:{}", chatClient.prompt("把xiaofuge转换为大写").call().content());
    }

    public McpSyncClient sseMcpClient02() {
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                .builder("http://127.0.0.1:8777")
                .sseEndpoint("/api-gateway/test10001/mcp/sse")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(36000)).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP02 Initialized {}", init_sse);

        return mcpSyncClient;
    }

    /**
     * http://appbuilder.baidu.com/v2/ai_search/mcp/sse?api_key=Bearer+bce-v3/ALTAK-3zODLb9qHozIftQlGwez5/2696e92781f5bf1ba1870e2958f239fd6dc822a4
     *
     * 百度搜索MCP服务(url)；https://sai.baidu.com/zh/detail/e014c6ffd555697deabf00d058baf388
     * 百度搜索MCP服务(key - 可自行申请)；https://console.bce.baidu.com/iam/?_=1753597622044#/iam/apikey/list
     */
    public McpSyncClient sseMcpClient01() {
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                .builder("http://appbuilder.baidu.com")
                .sseEndpoint("/v2/ai_search/mcp/sse?api_key=Bearer+bce-v3/ALTAK-3zODLb9qHozIftQlGwez5/2696e92781f5bf1ba1870e2958f239fd6dc822a4")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(36000)).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP01 Initialized {}", init_sse);

        return mcpSyncClient;
    }

    /**
     * 用于调试前面章节实现的mcp ai-mcp-gateway-demo-mcp-json-rpc
     *
     * 源码类；
     * - WebFluxSseServerTransportProvider
     * - McpSchema
     */
    public McpSyncClient sseMcpClient03() {
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                .builder("http://127.0.0.1:8777")
                .sseEndpoint("/sse")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(36000)).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP03 Initialized {}", init_sse);

        return mcpSyncClient;
    }

}
