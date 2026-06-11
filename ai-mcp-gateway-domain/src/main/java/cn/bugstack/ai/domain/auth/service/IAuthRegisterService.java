package cn.bugstack.ai.domain.auth.service;

import cn.bugstack.ai.domain.auth.model.entity.RegisterCommandEntity;

/**
 * 认证服务注册接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/13 08:34
 */
public interface IAuthRegisterService {

    /**
     * 注册
     */
    void register(RegisterCommandEntity commandEntity);

    /**
     * 删除
     */
    void deleteGatewayAuth(String gatewayId);

}
