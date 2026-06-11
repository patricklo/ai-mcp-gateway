package cn.bugstack.ai.domain.gateway.adapter.repository;

import cn.bugstack.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.bugstack.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;

/**
 * 网关仓储服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/21 07:57
 */
public interface IGatewayRepository {

    void saveGatewayConfig(GatewayConfigCommandEntity commandEntity);

    void updateGatewayAuthStatus(GatewayConfigCommandEntity commandEntity);

    void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity);

    void updateGatewayToolProtocol(GatewayToolConfigCommandEntity commandEntity);

    void deleteGatewayToolConfig(Long toolId);

}

