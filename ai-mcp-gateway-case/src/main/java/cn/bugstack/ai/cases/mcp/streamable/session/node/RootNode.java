package cn.bugstack.ai.cases.mcp.streamable.session.node;

import cn.bugstack.ai.cases.mcp.streamable.session.AbstractMcpStreamableSessionSupport;
import cn.bugstack.ai.cases.mcp.streamable.session.factory.DefaultMcpStreamableSessionFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * 根节点
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/5/20 07:03
 */
@Slf4j
@Service("mcpStreamableSessionRootNode")
public class RootNode extends AbstractMcpStreamableSessionSupport {

    @Resource(name = "mcpStreamableSessionVerifyNode")
    private VerifyNode verifyNode;

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String requestParameter, DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext) throws Exception {
        try {
            log.info("创建会话 mcp streamable session RootNode:{}", requestParameter);

            return router(requestParameter, dynamicContext);
        } catch (Exception e) {
            log.error("创建会话 mcp streamable session RootNode 异常:{}", requestParameter, e);
            throw e;
        }
    }

    @Override
    public StrategyHandler<String, DefaultMcpStreamableSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String requestParameter, DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext) throws Exception {
        return verifyNode;
    }

}
