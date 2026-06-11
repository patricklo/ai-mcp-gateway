package cn.bugstack.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关配置应答对象
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/24 08:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayConfigResponseDTO {

    private Boolean success;

}
