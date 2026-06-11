package cn.bugstack.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关工具查询实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolQueryEntity {

    /** 网关 ID */
    private String gatewayId;

    /** 工具 ID */
    private String toolId;

    /** 当前页 */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}
