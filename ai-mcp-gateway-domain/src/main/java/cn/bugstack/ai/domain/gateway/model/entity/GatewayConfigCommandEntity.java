package cn.bugstack.ai.domain.gateway.model.entity;

import cn.bugstack.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.bugstack.ai.types.enums.GatewayEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关配置实体
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/21 08:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigCommandEntity {

    private GatewayConfigVO gatewayConfigVO;

    public static GatewayConfigCommandEntity buildUpdateGatewayAuthStatusVO(String gatewayId, GatewayEnum.GatewayAuthStatusEnum auth) {
        return GatewayConfigCommandEntity.builder()
                .gatewayConfigVO(GatewayConfigVO.builder()
                        .gatewayId(gatewayId)
                        .auth(auth)
                        .build())
                .build();
    }

}
