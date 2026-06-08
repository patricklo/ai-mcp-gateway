package cn.bugstack.ai.domain.auth.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 校验证书命令实体对象
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/22 10:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseCommandEntity {

    /**
     * 网关ID
     */
    private String gatewayId;

    /**
     * API密钥
     */
    private String apiKey;

}
