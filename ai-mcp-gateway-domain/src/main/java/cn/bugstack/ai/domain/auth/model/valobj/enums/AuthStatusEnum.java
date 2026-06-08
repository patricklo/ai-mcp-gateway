package cn.bugstack.ai.domain.auth.model.valobj.enums;

import cn.bugstack.ai.types.enums.ResponseCode;
import cn.bugstack.ai.types.exception.AppException;
import lombok.*;

/**
 * 认证状态枚举
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/2/22 11:11
 */
public enum AuthStatusEnum {

    ;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum GatewayConfig {

        NOT_VERIFIED(0, "不校验"),

        STRONG_VERIFIED(1, "强校验"),
        ;

        private Integer code;
        private String info;

        public static GatewayConfig get(Integer code) {
            if (code == null) return null;
            for (GatewayConfig val : values()) {
                if (val.code.equals(code)) {
                    return val;
                }
            }
            throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum AuthConfig {
        DISABLE(0, "禁用"),
        ENABLE(1, "启用"),

        ;

        private Integer code;
        private String info;

        public static AuthConfig get(Integer code) {
            if (code == null) return null;
            for (AuthConfig val : values()) {
                if (val.code.equals(code)) {
                    return val;
                }
            }
            throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
        }
    }

}
