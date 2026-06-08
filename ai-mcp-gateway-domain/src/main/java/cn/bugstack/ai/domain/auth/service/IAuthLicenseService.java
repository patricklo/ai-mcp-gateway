package cn.bugstack.ai.domain.auth.service;

import cn.bugstack.ai.domain.auth.model.entity.LicenseCommandEntity;

/**
 * 权限证书服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/22 10:11
 */
public interface IAuthLicenseService {

    boolean checkLicense(LicenseCommandEntity commandEntity);

}
