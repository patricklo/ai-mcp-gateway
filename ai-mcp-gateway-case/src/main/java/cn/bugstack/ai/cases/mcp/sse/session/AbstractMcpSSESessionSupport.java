package cn.bugstack.ai.cases.mcp.sse.session;

import cn.bugstack.ai.cases.mcp.sse.session.factory.DefaultMcpSSESessionFactory;
import cn.bugstack.ai.domain.session.service.ISessionManagementService;
import cn.bugstack.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractMcpSSESessionSupport extends AbstractMultiThreadStrategyRouter<String, DefaultMcpSSESessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> {

    @Resource
    protected ISessionManagementService sessionManagementService;

    @Override
    protected void multiThread(String requestParameter, DefaultMcpSSESessionFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {

    }

}
