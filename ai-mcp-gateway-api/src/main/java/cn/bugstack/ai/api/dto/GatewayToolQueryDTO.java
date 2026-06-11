package cn.bugstack.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 网关工具查询 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolQueryDTO implements Serializable {

    /** 网关 ID */
    private String gatewayId;

    /** 工具 ID */
    private String toolId;

    /** 当前页 */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}
