package cn.bugstack.ai.domain.gateway.model.entity;

import cn.bugstack.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关工具配置实体
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/21 08:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolConfigCommandEntity {

    private GatewayToolConfigVO gatewayToolConfigVO;

    public static GatewayToolConfigCommandEntity buildUpdateGatewayProtocol(String gatewayId, Long toolId, Long protocolId, String protocolType) {
        return GatewayToolConfigCommandEntity.builder()
                .gatewayToolConfigVO(
                        GatewayToolConfigVO.builder()
                                .gatewayId(gatewayId)
                                .toolId(toolId)
                                .protocolId(protocolId)
                                .protocolType(protocolType)
                                .build())
                .build();
    }

}
