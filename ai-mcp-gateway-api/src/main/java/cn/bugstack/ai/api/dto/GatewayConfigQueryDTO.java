package cn.bugstack.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 网关配置查询 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigQueryDTO implements Serializable {

    /** 网关 ID */
    private String gatewayId;

    /** 网关名称 */
    private String gatewayName;

    /** 当前页 */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}
