package cn.bugstack.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayAuthDTO {

    private String gatewayId;
    private String apiKey;
    private Integer rateLimit;
    private Date expireTime;

}