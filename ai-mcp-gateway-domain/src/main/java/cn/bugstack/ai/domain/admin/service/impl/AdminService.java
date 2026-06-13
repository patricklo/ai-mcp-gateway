package cn.bugstack.ai.domain.admin.service.impl;

import cn.bugstack.ai.domain.admin.adapter.respository.IAdminRepository;
import cn.bugstack.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayConfigPageEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayConfigQueryEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayProtocolConfigEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayProtocolPageEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayProtocolQueryEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayToolConfigEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayToolPageEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayToolQueryEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayAuthConfigEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayAuthPageEntity;
import cn.bugstack.ai.domain.admin.model.entity.GatewayAuthQueryEntity;
import cn.bugstack.ai.domain.admin.service.IAdminService;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/26
 */
@Service
public class AdminService implements IAdminService {

    @Resource
    private IAdminRepository adminRepository;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        return adminRepository.queryGatewayConfigList();
    }

    @Override
    public GatewayConfigPageEntity queryGatewayConfigPage(GatewayConfigQueryEntity queryEntity) {
        return adminRepository.queryGatewayConfigPage(queryEntity);
    }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolList() {
        return adminRepository.queryGatewayToolList();
    }

    @Override
    public GatewayToolPageEntity queryGatewayToolPage(GatewayToolQueryEntity queryEntity) {
        return adminRepository.queryGatewayToolPage(queryEntity);
    }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolListByGatewayId(String gatewayId) {
        return adminRepository.queryGatewayToolListByGatewayId(gatewayId);
    }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolList() {
        return adminRepository.queryGatewayProtocolList();
    }

    @Override
    public GatewayProtocolPageEntity queryGatewayProtocolPage(GatewayProtocolQueryEntity queryEntity) {
        return adminRepository.queryGatewayProtocolPage(queryEntity);
    }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolListByGatewayId(String gatewayId) {
        List<GatewayToolConfigEntity> tools = adminRepository.queryGatewayToolListByGatewayId(gatewayId);
        if (tools == null || tools.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<Long> protocolIds = tools.stream()
                .map(GatewayToolConfigEntity::getProtocolId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        
        return adminRepository.queryGatewayProtocolListByProtocolIds(protocolIds);
    }

    @Override
    public List<GatewayAuthConfigEntity> queryGatewayAuthList() {
        return adminRepository.queryGatewayAuthList();
    }

    @Override
    public GatewayAuthPageEntity queryGatewayAuthPage(GatewayAuthQueryEntity queryEntity) {
        return adminRepository.queryGatewayAuthPage(queryEntity);
    }

    @Override
    public List<GatewayAuthConfigEntity> queryGatewayAuthListByGatewayId(String gatewayId) {
        // 直接委托给 Repository 层按网关ID查询，不在领域服务中做额外业务逻辑
        return adminRepository.queryGatewayAuthListByGatewayId(gatewayId);
    }

}