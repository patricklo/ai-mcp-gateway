package cn.bugstack.ai.domain.gateway.service.gateway;

import cn.bugstack.ai.domain.gateway.adapter.repository.IGatewayRepository;
import cn.bugstack.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.bugstack.ai.domain.gateway.service.IGatewayConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 网关配置服务
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/21 08:01
 */
@Slf4j
@Service
public class GatewayConfigService implements IGatewayConfigService {

    @Resource
    private IGatewayRepository repository;

    @Override
    public void saveGatewayConfig(GatewayConfigCommandEntity commandEntity) {
        repository.saveGatewayConfig(commandEntity);
    }

    @Override
    public void updateGatewayAuthStatus(GatewayConfigCommandEntity commandEntity) {
        repository.updateGatewayAuthStatus(commandEntity);
    }
}
