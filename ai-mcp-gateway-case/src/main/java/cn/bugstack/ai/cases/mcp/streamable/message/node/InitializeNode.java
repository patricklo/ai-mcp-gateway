package cn.bugstack.ai.cases.mcp.streamable.message.node;

import cn.bugstack.ai.cases.mcp.streamable.message.AbstractMcpStreamableMessageServiceSupport;
import cn.bugstack.ai.cases.mcp.streamable.message.factory.DefaultMcpStreamableMessageFactory;
import cn.bugstack.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.bugstack.ai.domain.auth.service.IAuthLicenseService;
import cn.bugstack.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.bugstack.ai.domain.session.model.valobj.McpSchemaVO;
import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;
import cn.bugstack.ai.domain.session.model.valobj.enums.SessionTransportTypeEnumVO;
import cn.bugstack.ai.types.enums.McpErrorCodes;
import cn.bugstack.ai.types.exception.AppException;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Streamable initialize 节点
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/5/25 08:00
 */
@Slf4j
@Service("mcpStreamableMessageInitializeNode")
public class InitializeNode extends AbstractMcpStreamableMessageServiceSupport {

    @Resource
    private IAuthLicenseService authLicenseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected ResponseEntity<?> doApply(HandleMessageCommandEntity requestParameter, DefaultMcpStreamableMessageFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Streamable 消息处理 InitializeNode:{}", requestParameter);

        boolean isCheckSuccess = authLicenseService.checkLicense(new LicenseCommandEntity(requestParameter.getGatewayId(), requestParameter.getApiKey()));
        if (!isCheckSuccess) {
            throw new AppException(McpErrorCodes.INSUFFICIENT_PERMISSIONS, "fail to auth apikey");
        }

        SessionConfigVO sessionConfigVO = sessionManagementService.createSession(
                requestParameter.getGatewayId(),
                requestParameter.getApiKey(),
                SessionTransportTypeEnumVO.STREAMABLE);
        dynamicContext.setSessionConfigVO(sessionConfigVO);

        McpSchemaVO.JSONRPCResponse jsonrpcResponse = serviceMessageService.processHandlerMessage(requestParameter);
        String responseJson = objectMapper.writeValueAsString(jsonrpcResponse);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Mcp-Session-Id", sessionConfigVO.getSessionId())
                .body(responseJson);
    }

    @Override
    public StrategyHandler<HandleMessageCommandEntity, DefaultMcpStreamableMessageFactory.DynamicContext, ResponseEntity<?>> get(HandleMessageCommandEntity requestParameter, DefaultMcpStreamableMessageFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }

}
