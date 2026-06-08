package cn.bugstack.ai.cases.mcp.session.node;

import cn.bugstack.ai.cases.mcp.session.AbstractMcpSessionSupport;
import cn.bugstack.ai.cases.mcp.session.factory.DefaultMcpSessionFactory;
import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

/**
 * 结束节点
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 09:25
 */
@Slf4j
@Service("mcpSessionEndNode")
public class EndNode extends AbstractMcpSessionSupport {

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String requestParameter, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        log.info("创建会话-EndNode:{}", requestParameter);

        // 获取上下文
        SessionConfigVO sessionConfigVO = dynamicContext.getSessionConfigVO();
        String sessionId = sessionConfigVO.getSessionId();

        Sinks.Many<ServerSentEvent<String>> sink = sessionConfigVO.getSink();

        return sink.asFlux()
                .mergeWith(
                        // 心跳机制 - 防止连接超时，延长间隔避免干扰正常通信
                        Flux.interval(Duration.ofSeconds(60))
                                .map(i -> ServerSentEvent.<String>builder()
                                        .event("ping")
                                        .data("ping")
                                        .build())
                )
                // 连接取消时的清理逻辑
                .doOnCancel(() -> {
                    log.info("SSE连接取消，会话ID: {}", sessionId);
                    sessionManagementService.removeSession(sessionId);
                })
                // 连接终止时的清理逻辑
                .doOnTerminate(() -> {
                    log.info("SSE连接终止，会话ID: {}", sessionId);
                    sessionManagementService.removeSession(sessionId);
                });
    }

    @Override
    public StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String requestParameter, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }

}
