package cn.bugstack.ai.cases.mcp.session;

import cn.bugstack.ai.cases.mcp.IMcpSessionService;
import cn.bugstack.ai.cases.mcp.session.factory.DefaultMcpSessionFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * 会话服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 09:08
 */
@Service
public class McpSessionService implements IMcpSessionService {

    @Resource
    private DefaultMcpSessionFactory defaultMcpSessionFactory;

    @Override
    public Flux<ServerSentEvent<String>> createMcpSession(String gatewayId, String apiKey) throws Exception {

        StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> strategyHandler =
                defaultMcpSessionFactory.strategyHandler();

        DefaultMcpSessionFactory.DynamicContext dynamicContext = new DefaultMcpSessionFactory.DynamicContext();
        dynamicContext.setApiKey(apiKey);

        return strategyHandler.apply(gatewayId, dynamicContext);
    }

}
