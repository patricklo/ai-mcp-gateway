package cn.bugstack.ai.cases.mcp.session.node;

import cn.bugstack.ai.cases.mcp.session.AbstractMcpSessionSupport;
import cn.bugstack.ai.cases.mcp.session.factory.DefaultMcpSessionFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * 鉴权核验 - （todo 这部分后续实现）
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 09:22
 */
@Service
public class VerifyNode extends AbstractMcpSessionSupport {

    @Resource
    private SessionNode sessionNode;

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String requestParameter, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String requestParameter, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        return sessionNode;
    }

}
