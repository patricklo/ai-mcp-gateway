package cn.bugstack.ai.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户网关权限表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpGatewayAuthPO {

    /**
     * 主键ID
     */
    private Long id;
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
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

}
