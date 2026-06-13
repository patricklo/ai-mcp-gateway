package cn.bugstack.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 大模型请求测试
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/4/8 08:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayLLMRequestDTO {

    /**
     * 网关ID
     */
    private String gatewayId;

    /**
     * 认证Key
     */
    private String authApiKey;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * 请求信息
     */
    private String message;

    /**
     * 重新加载LLM，当有协议更新时，可以传入入参
     */
    private boolean reload = false;

    private String mcpType = "sse";

}
