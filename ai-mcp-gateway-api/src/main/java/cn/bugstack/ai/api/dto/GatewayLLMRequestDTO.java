package cn.bugstack.ai.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayLLMRequestDTO {
    private String gatewayId;
    private String authApiKey;
    private Integer timeout;
    private String message;
    private boolean reload = false;
}
