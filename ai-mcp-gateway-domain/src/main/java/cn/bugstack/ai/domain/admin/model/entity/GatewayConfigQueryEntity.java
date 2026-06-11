package cn.bugstack.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关配置查询实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigQueryEntity {

    /** 网关 ID */
    private String gatewayId;

    /** 网关名称 */
    private String gatewayName;

    /** 当前页 */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}
