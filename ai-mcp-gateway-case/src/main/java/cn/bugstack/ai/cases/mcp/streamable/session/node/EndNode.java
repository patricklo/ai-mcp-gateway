package cn.bugstack.ai.cases.mcp.streamable.session.node;

import cn.bugstack.ai.cases.mcp.streamable.session.AbstractMcpStreamableSessionSupport;
import cn.bugstack.ai.cases.mcp.streamable.session.factory.DefaultMcpStreamableSessionFactory;
import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * 结束节点
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/5/20 07:13
 */
@Slf4j
@Service("mcpStreamableSessionEndNode")
public class EndNode extends AbstractMcpStreamableSessionSupport {

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String requestParameter, DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext) throws Exception {
        log.info("获取 Streamable 会话-EndNode gatewayId:{} sessionId:{}", dynamicContext.getGatewayId(), requestParameter);

        SessionConfigVO sessionConfigVO = dynamicContext.getSessionConfigVO();
        String sessionId = sessionConfigVO.getSessionId();

        return sessionConfigVO.getSink().asFlux()
                .mergeWith(Flux.interval(Duration.ofSeconds(60))
                        .map(i -> ServerSentEvent.<String>builder()
                                .event("ping")
                                .data("ping")
                                .build()))
                .doOnCancel(() -> log.info("Streamable SSE 监听取消，会话ID: {}", sessionId))
                .doOnTerminate(() -> log.info("Streamable SSE 监听终止，会话ID: {}", sessionId));
    }

    @Override
    public StrategyHandler<String, DefaultMcpStreamableSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String requestParameter, DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }
}
