package cn.bugstack.ai.cases.admin.gateway;

import cn.bugstack.ai.cases.admin.IAdminGatewayService;
import cn.bugstack.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.bugstack.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.bugstack.ai.domain.gateway.service.IGatewayConfigService;
import cn.bugstack.ai.domain.gateway.service.IGatewayToolConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 网关配置管理
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/24 08:12
 */
@Slf4j
@Service
public class AdminGatewayService implements IAdminGatewayService {

    @Resource
    private IGatewayConfigService gatewayConfigService;

    @Resource
    private IGatewayToolConfigService gatewayToolConfigService;

    @Override
    public void saveGatewayConfig(GatewayConfigCommandEntity commandEntity) {
        gatewayConfigService.saveGatewayConfig(commandEntity);
    }

    @Override
    public void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity) {
        gatewayToolConfigService.saveGatewayToolConfig(commandEntity);
    }

    @Override
    public void deleteGatewayToolConfig(Long toolId) {
        gatewayToolConfigService.deleteGatewayToolConfig(toolId);
    }

}
