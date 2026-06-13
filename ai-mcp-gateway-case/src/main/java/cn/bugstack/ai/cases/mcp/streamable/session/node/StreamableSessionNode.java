package cn.bugstack.ai.cases.mcp.streamable.session.node;

import cn.bugstack.ai.cases.mcp.streamable.session.AbstractMcpStreamableSessionSupport;
import cn.bugstack.ai.cases.mcp.streamable.session.factory.DefaultMcpStreamableSessionFactory;
import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;
import cn.bugstack.ai.types.enums.McpErrorCodes;
import cn.bugstack.ai.types.exception.AppException;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * 会话节点
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/5/20 07:14
 */
@Slf4j
@Service("mcpStreamableSessionNode")
public class StreamableSessionNode extends AbstractMcpStreamableSessionSupport {

    @Resource(name = "mcpStreamableSessionEndNode")
    private EndNode endNode;

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String requestParameter, DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext) throws Exception {
        log.info("获取 Streamable 会话-SessionNode gatewayId:{} sessionId:{}", dynamicContext.getGatewayId(), requestParameter);

        SessionConfigVO sessionConfigVO = sessionManagementService.getSession(requestParameter);
        if (null == sessionConfigVO) {
            log.warn("Streamable 会话不存在或已过期，gatewayId:{} sessionId:{}", dynamicContext.getGatewayId(), requestParameter);
            throw new AppException(McpErrorCodes.SESSION_NOT_FOUND, "session not found: " + requestParameter);
        }

        dynamicContext.setSessionConfigVO(sessionConfigVO);

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DefaultMcpStreamableSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String requestParameter, DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext) throws Exception {
        return endNode;
    }

}
