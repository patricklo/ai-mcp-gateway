package cn.bugstack.ai.domain.auth.adapter.repository;

import cn.bugstack.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.bugstack.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.bugstack.ai.domain.auth.model.valobj.enums.AuthStatusEnum;

/**
 * 鉴权仓储服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/22 10:57
 */
public interface IAuthRepository {

    int queryEffectiveGatewayAuthCount(String gatewayId);

    McpGatewayAuthVO queryEffectiveGatewayAuthInfo(LicenseCommandEntity commandEntity);

    void insert(McpGatewayAuthVO mcpGatewayAuthVO);

    AuthStatusEnum.GatewayConfig queryGatewayAuthStatus(String gatewayId);

}
