package cn.bugstack.ai.domain.session.model.valobj.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 网关协议配置，值对象
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/1/13 08:16
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpGatewayConfigVO {

    /**
     * 网关ID
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

}
