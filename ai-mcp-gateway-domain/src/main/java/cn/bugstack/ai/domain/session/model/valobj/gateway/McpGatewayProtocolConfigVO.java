package cn.bugstack.ai.domain.session.model.valobj.gateway;

import lombok.*;

/**
 * 协议配置
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/1/30 20:24
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpGatewayProtocolConfigVO {

    private HTTPConfig httpConfig;

    @Data
    public static class HTTPConfig {
        private String httpUrl;
        private String httpHeaders;
        private String httpMethod;
        private Integer timeout;
    }

}
