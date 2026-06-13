package cn.bugstack.ai.cases.mcp.streamable.message;

import cn.bugstack.ai.cases.mcp.streamable.message.factory.DefaultMcpStreamableMessageFactory;
import cn.bugstack.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.bugstack.ai.domain.session.service.ISessionManagementService;
import cn.bugstack.ai.domain.session.service.ISessionMessageService;
import cn.bugstack.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractMcpStreamableMessageServiceSupport extends AbstractMultiThreadStrategyRouter<HandleMessageCommandEntity, DefaultMcpStreamableMessageFactory.DynamicContext, ResponseEntity<?>> {

    @Resource
    protected ISessionMessageService serviceMessageService;

    @Resource
    protected ISessionManagementService sessionManagementService;

    @Override
    protected void multiThread(HandleMessageCommandEntity requestParameter, DefaultMcpStreamableMessageFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {

    }

}
