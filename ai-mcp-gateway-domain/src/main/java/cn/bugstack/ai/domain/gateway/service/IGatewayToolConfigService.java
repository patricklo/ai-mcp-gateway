package cn.bugstack.ai.domain.gateway.service;

import cn.bugstack.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;

/**
 * 网关工具配置服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/21 09:43
 */
public interface IGatewayToolConfigService {

    void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity);

    void updateGatewayToolProtocol(GatewayToolConfigCommandEntity commandEntity);

    void deleteGatewayToolConfig(Long toolId);

}
