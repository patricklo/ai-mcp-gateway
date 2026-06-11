package cn.bugstack.ai.domain.gateway.model.valobj;

import cn.bugstack.ai.types.enums.GatewayEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 网关配置值对象
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/21 08:06
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayConfigVO {

    /**
     * 网关唯一标识
     */
    private String gatewayId;
    /**
     * 网关名称
     */
    private String gatewayName;
    /**
     * 网关描述
     */
    private String gatewayDesc;
    /**
     * 协议版本
     */
    private String version;
    /**
     * 校验状态：0-禁用，1-启用
     */
    private GatewayEnum.GatewayAuthStatusEnum auth;
    /**
     * 网关状态：0-不校验，1-强校验
     */
    private GatewayEnum.GatewayStatus status;

}
