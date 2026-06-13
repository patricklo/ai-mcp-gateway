package cn.bugstack.ai.cases.mcp.streamable.message.node;

import cn.bugstack.ai.cases.mcp.streamable.message.AbstractMcpStreamableMessageServiceSupport;
import cn.bugstack.ai.cases.mcp.streamable.message.factory.DefaultMcpStreamableMessageFactory;
import cn.bugstack.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会话节点
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/5/20 07:40
 */
@Slf4j
@Service("mcpStreamableMessageSessionNode")
public class SessionNode extends AbstractMcpStreamableMessageServiceSupport {

    @Resource(name = "mcpStreamableMessageHandlerNode")
    private MessageHandlerNode messageHandlerNode;

    @Override
    protected ResponseEntity<?> doApply(HandleMessageCommandEntity requestParameter, DefaultMcpStreamableMessageFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Streamable 消息处理 SessionNode:{}", requestParameter);

        SessionConfigVO sessionConfigVO = sessionManagementService.getSession(requestParameter.getSessionId());
        if (null == sessionConfigVO) {
            log.warn("Streamable 会话不存在或已过期，gatewayId:{} sessionId:{}", requestParameter.getGatewayId(), requestParameter.getSessionId());
            return ResponseEntity.notFound().build();
        }

        dynamicContext.setSessionConfigVO(sessionConfigVO);

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<HandleMessageCommandEntity, DefaultMcpStreamableMessageFactory.DynamicContext, ResponseEntity<?>> get(HandleMessageCommandEntity requestParameter, DefaultMcpStreamableMessageFactory.DynamicContext dynamicContext) throws Exception {
        return messageHandlerNode;
    }

}
