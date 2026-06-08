package cn.bugstack.ai.domain.auth.model.valobj;

import cn.bugstack.ai.domain.auth.model.valobj.enums.AuthStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 网关服务认证值对象
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/22 10:23
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpGatewayAuthVO {

    /**
     * 网关ID
     */
    private String gatewayId;
    /**
     * API密钥
     */
    private String apiKey;
    /**
     * 速率限制（次/小时）
     */
    private Integer rateLimit;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 状态：0-禁用，1-启用
     */
    private AuthStatusEnum.AuthConfig status;

}
