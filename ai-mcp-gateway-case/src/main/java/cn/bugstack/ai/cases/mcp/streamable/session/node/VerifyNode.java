package cn.bugstack.ai.cases.mcp.streamable.session.node;

import cn.bugstack.ai.cases.mcp.streamable.session.AbstractMcpStreamableSessionSupport;
import cn.bugstack.ai.cases.mcp.streamable.session.factory.DefaultMcpStreamableSessionFactory;
import cn.bugstack.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.bugstack.ai.domain.auth.service.IAuthLicenseService;
import cn.bugstack.ai.types.enums.McpErrorCodes;
import cn.bugstack.ai.types.exception.AppException;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * 验证节点
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/5/20 07:14
 */
@Slf4j
@Service("mcpStreamableSessionVerifyNode")
public class VerifyNode extends AbstractMcpStreamableSessionSupport {

    @Resource(name = "mcpStreamableSessionNode")
    private StreamableSessionNode sessionNode;

    @Resource
    private IAuthLicenseService authLicenseService;

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String requestParameter, DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext) throws Exception {
        log.info("获取 Streamable 会话-VerifyNode gatewayId:{} sessionId:{}", dynamicContext.getGatewayId(), requestParameter);

        boolean isCheckSuccess = authLicenseService.checkLicense(new LicenseCommandEntity(dynamicContext.getGatewayId(), dynamicContext.getApiKey()));
        if (!isCheckSuccess) {
            throw new AppException(McpErrorCodes.INSUFFICIENT_PERMISSIONS, "fail to auth apikey");
        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DefaultMcpStreamableSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String requestParameter, DefaultMcpStreamableSessionFactory.DynamicContext dynamicContext) throws Exception {
        return sessionNode;
    }

}
