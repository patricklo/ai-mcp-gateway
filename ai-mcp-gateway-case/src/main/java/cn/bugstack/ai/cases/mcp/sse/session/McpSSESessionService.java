package cn.bugstack.ai.cases.mcp.sse.session;

import cn.bugstack.ai.cases.mcp.IMcpSessionService;
import cn.bugstack.ai.cases.mcp.sse.session.factory.DefaultMcpSSESessionFactory;
import cn.bugstack.ai.types.exception.AppException;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

import static cn.bugstack.ai.types.enums.ResponseCode.METHOD_NOT_FOUND;

/**
 * 会话服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 09:08
 */
@Service("mcpSSESessionService")
public class McpSSESessionService implements IMcpSessionService {

    @Resource
    private DefaultMcpSSESessionFactory defaultMcpSSESessionFactory;

    @Override
    public Flux<ServerSentEvent<String>> createMcpSession(String gatewayId, String apiKey) throws Exception {

        StrategyHandler<String, DefaultMcpSSESessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> strategyHandler =
                defaultMcpSSESessionFactory.strategyHandler();

        DefaultMcpSSESessionFactory.DynamicContext dynamicContext = new DefaultMcpSSESessionFactory.DynamicContext();
        dynamicContext.setApiKey(apiKey);

        return strategyHandler.apply(gatewayId, dynamicContext);
    }

    @Override
    public Flux<ServerSentEvent<String>> getMcpSession(String gatewayId, String apiKey, String sessionId) throws Exception {
        throw new AppException(METHOD_NOT_FOUND.getCode(), METHOD_NOT_FOUND.getInfo());
    }

    @Override
    public void deleteMcpSession(String sessionId) {
        throw new AppException(METHOD_NOT_FOUND.getCode(), METHOD_NOT_FOUND.getInfo());
    }

}
