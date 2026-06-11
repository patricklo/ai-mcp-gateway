package cn.bugstack.ai.cases.admin.llm;


import cn.bugstack.ai.domain.gateway.service.IGatewayToolConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminLLMService implements IAdminLLMServie {
    @Value("${server.servlet.context-path}")
    private String baseUrlContextPath;

    @Value("${server.port}")
    private Integer port;

    @Resource
    private ILLMService llmService;

    @Resource
    private IGatewayToolConfigService gatewayToolConfigService;

    @Override

}
