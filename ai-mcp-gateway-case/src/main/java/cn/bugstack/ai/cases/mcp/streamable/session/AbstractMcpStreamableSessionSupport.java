package cn.bugstack.ai.cases.mcp.streamable.session;

import cn.bugstack.ai.cases.mcp.streamable.session.factory.DefaultMcpStreamableSessionFactory;
import cn.bugstack.ai.domain.session.service.ISessionManagementService;
import cn.bugstack.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractMcpStreamableSessionSupport extends AbstractMultiThreadStrategyRouter<String, DefaultMcpStreamableSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> {

    @Resource
    protected ISessionManagementService sessionManagementService;

    @Override
    protected void multiThread(String requestParameter, DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {

    }

}
