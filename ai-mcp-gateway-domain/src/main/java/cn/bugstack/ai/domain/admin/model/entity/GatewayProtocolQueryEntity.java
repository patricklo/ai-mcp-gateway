package cn.bugstack.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关协议查询实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayProtocolQueryEntity {

    /** 协议 ID */
    private Long protocolId;

    /** 请求地址 (模糊匹配) */
    private String httpUrl;

    /** 当前页 */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}
