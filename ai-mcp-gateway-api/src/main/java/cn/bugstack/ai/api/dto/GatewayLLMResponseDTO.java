package cn.bugstack.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大模型应答结果
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/4/8 08:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayLLMResponseDTO {

    private String content;

}
