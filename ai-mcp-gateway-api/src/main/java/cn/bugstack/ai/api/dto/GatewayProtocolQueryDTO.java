package cn.bugstack.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 网关协议查询 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayProtocolQueryDTO implements Serializable {

    /** 协议 ID */
    private Long protocolId;

    /** 请求地址 (模糊匹配) */
    private String httpUrl;

    /** 当前页 */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}
