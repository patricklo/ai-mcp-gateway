package cn.bugstack.ai.cases.mcp.message;

import cn.bugstack.ai.cases.mcp.IMcpMessageService;
import cn.bugstack.ai.cases.mcp.message.factory.DefaultMcpMessageFactory;
import cn.bugstack.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 会话消息处理
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/20 07:37
 */
@Slf4j
@Service
public class McpMessageService implements IMcpMessageService {

    @Resource
    private DefaultMcpMessageFactory defaultMcpMessageFactory;

    @Override
    public ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception {
        StrategyHandler<HandleMessageCommandEntity, DefaultMcpMessageFactory.DynamicContext, ResponseEntity<Void>> strategyHandler
                = defaultMcpMessageFactory.strategyHandler();

        return strategyHandler.apply(commandEntity, new DefaultMcpMessageFactory.DynamicContext());
    }

}
