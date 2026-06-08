package cn.bugstack.ai.domain.auth.service.license;

import cn.bugstack.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.bugstack.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.bugstack.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.bugstack.ai.domain.auth.model.valobj.enums.AuthStatusEnum;
import cn.bugstack.ai.domain.auth.service.IAuthLicenseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 权限证书服务
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/22 10:19
 */
@Slf4j
@Service
public class AuthLicenseService implements IAuthLicenseService {

    @Resource
    private IAuthRepository repository;

    @Override
    public boolean checkLicense(LicenseCommandEntity commandEntity) {
        // 查询是否强校验(非强校验，直接返回校验结果 true)
        AuthStatusEnum.GatewayConfig gatewayAuthStatus = repository.queryGatewayAuthStatus(commandEntity.getGatewayId());
        if (AuthStatusEnum.GatewayConfig.NOT_VERIFIED.equals(gatewayAuthStatus)) return true;

        // 查询网关认证配置信息
        McpGatewayAuthVO mcpGatewayAuthVO = repository.queryEffectiveGatewayAuthInfo(commandEntity);

        // 没有匹配到权限返回 false
        if (null == mcpGatewayAuthVO) return false;

        // 检查是否开启了认证模式，未开启则为false
        if (AuthStatusEnum.AuthConfig.DISABLE.equals(mcpGatewayAuthVO.getStatus())) {
            return false;
        }

        // 判断过期时间，未设置过期时间永久有效
        Date expireTime = mcpGatewayAuthVO.getExpireTime();
        if (null == expireTime) return true;

        boolean isBefore = new Date().before(expireTime);

        if (!isBefore) {
            log.warn("apiKey 权限校验，expireTime 已过期。gatewayId:{} apiKey:{}", commandEntity.getGatewayId(), commandEntity.getApiKey());
        }

        return isBefore;
    }

}
