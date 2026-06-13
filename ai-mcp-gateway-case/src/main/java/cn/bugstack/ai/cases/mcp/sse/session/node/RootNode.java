package cn.bugstack.ai.cases.mcp.sse.session.node;

import cn.bugstack.ai.cases.mcp.sse.session.AbstractMcpSSESessionSupport;
import cn.bugstack.ai.cases.mcp.sse.session.factory.DefaultMcpSSESessionFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * 根节点
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 09:10
 */
@Slf4j
@Service("mcpSessionRootNode")
public class RootNode extends AbstractMcpSSESessionSupport {

    @Resource(name = "mcpSessionVerifyNode")
    private VerifyNode verifyNode;

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String requestParameter, DefaultMcpSSESessionFactory.DynamicContext dynamicContext) throws Exception {
        try {
            log.info("创建会话 mcp session RootNode:{}", requestParameter);

            return router(requestParameter, dynamicContext);
        } catch (Exception e) {
            log.error("创建会话 mcp session RootNode 异常:{}", requestParameter, e);
            throw e;
        }
    }

    @Override
    public StrategyHandler<String, DefaultMcpSSESessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String requestParameter, DefaultMcpSSESessionFactory.DynamicContext dynamicContext) throws Exception {
        return verifyNode;
    }

}
