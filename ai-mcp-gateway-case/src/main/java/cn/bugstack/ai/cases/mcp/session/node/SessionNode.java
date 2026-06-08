package cn.bugstack.ai.cases.mcp.session.node;

import cn.bugstack.ai.cases.mcp.session.AbstractMcpSessionSupport;
import cn.bugstack.ai.cases.mcp.session.factory.DefaultMcpSessionFactory;
import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * 会话节点
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 09:23
 */
@Slf4j
@Service("mcpSessionSessionNode")
public class SessionNode extends AbstractMcpSessionSupport {

    @Resource(name = "mcpSessionEndNode")
    private EndNode endNode;

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String requestParameter, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        log.info("创建会话-SessionNode:{}", requestParameter);

        // 创建会话服务
        SessionConfigVO sessionConfigVO = sessionManagementService.createSession(requestParameter, dynamicContext.getApiKey());

        // 写入上下文中
        dynamicContext.setSessionConfigVO(sessionConfigVO);

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String requestParameter, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        return endNode;
    }

}
