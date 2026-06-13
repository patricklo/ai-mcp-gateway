package cn.bugstack.ai.cases.mcp.streamable.session;

import cn.bugstack.ai.cases.mcp.IMcpSessionService;
import cn.bugstack.ai.cases.mcp.streamable.session.factory.DefaultMcpStreamableSessionFactory;
import cn.bugstack.ai.domain.session.service.ISessionManagementService;
import cn.bugstack.ai.types.exception.AppException;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

import static cn.bugstack.ai.types.enums.ResponseCode.METHOD_NOT_FOUND;

/**
 * Streamable 会话服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/5/19 07:55
 */
@Service("mcpStreamableSessionService")
public class McpStreamableSessionService implements IMcpSessionService {

    @Resource
    private DefaultMcpStreamableSessionFactory defaultMcpStreamableSessionFactory;

    @Resource
    private ISessionManagementService sessionManagementService;

    /**
     * Streamable HTTP 会话由 POST initialize 创建，GET 只负责监听已有会话。
     */
    @Override
    public Flux<ServerSentEvent<String>> createMcpSession(String gatewayId, String apiKey) throws Exception {
        throw new AppException(METHOD_NOT_FOUND.getCode(), METHOD_NOT_FOUND.getInfo());
    }

    @Override
    public Flux<ServerSentEvent<String>> getMcpSession(String gatewayId, String apiKey, String sessionId) throws Exception {
        StrategyHandler<String, DefaultMcpStreamableSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> strategyHandler =
                defaultMcpStreamableSessionFactory.strategyHandler();

        DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext = new DefaultMcpStreamableSessionFactory.DynamicContext();
        dynamicContext.setGatewayId(gatewayId);
        dynamicContext.setApiKey(apiKey);

        return strategyHandler.apply(sessionId, dynamicContext);
    }

    public void deleteMcpSession(String sessionId) {
        sessionManagementService.removeSession(sessionId);
    }

}
