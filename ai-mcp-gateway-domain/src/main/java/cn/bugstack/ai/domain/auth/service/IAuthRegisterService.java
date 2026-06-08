package cn.bugstack.ai.domain.auth.service;

import cn.bugstack.ai.domain.auth.model.entity.RegisterCommandEntity;

/**
 * 权限注册服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/22 10:16
 */
public interface IAuthRegisterService {

    String register(RegisterCommandEntity commandEntity);

}
