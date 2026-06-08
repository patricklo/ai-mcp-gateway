package cn.bugstack.ai.domain.auth.model.entity;

import cn.bugstack.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 注册命令实体
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/22 10:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCommandEntity {

    /**
     * 网关ID
     */
    private String gatewayId;
    /**
     * 速率限制（次/小时）
     */
    private Integer rateLimit;
    /**
     * 过期时间
     */
    private Date expireTime;

}
