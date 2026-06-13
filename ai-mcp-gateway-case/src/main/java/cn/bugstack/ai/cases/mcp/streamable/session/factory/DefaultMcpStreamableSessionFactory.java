package cn.bugstack.ai.cases.mcp.streamable.session.factory;

import cn.bugstack.ai.cases.mcp.streamable.session.node.RootNode;
import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * MCP Streamable 会话服务工厂
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/5/20 07:04
 */
@Service
public class DefaultMcpStreamableSessionFactory {

    @Resource(name = "mcpStreamableSessionRootNode")
    private RootNode rootNode;

    public StrategyHandler<String, DefaultMcpStreamableSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> strategyHandler() {
        return rootNode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext {

        private String gatewayId;

        private String apiKey;

        private SessionConfigVO sessionConfigVO;
    }

}
