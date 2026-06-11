package cn.bugstack.ai.types.enums;

import cn.bugstack.ai.types.exception.AppException;
import lombok.Getter;

/**
 * 网关枚举；共用枚举抽取。
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/21 09:30
 */
public enum GatewayEnum {

    ;

    @Getter
    public enum GatewayStatus {

        NOT_VERIFIED(0, "不校验"),

        STRONG_VERIFIED(1, "强校验"),
        ;

        private final Integer code;
        private final String info;

        GatewayStatus(Integer code, String info) {
            this.code = code;
            this.info = info;
        }

        public static GatewayStatus get(Integer code) {
            if (code == null) return null;
            for (GatewayStatus val : values()) {
                if (val.code.equals(code)) {
                    return val;
                }
            }
            throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
        }
    }

    @Getter
    public enum GatewayAuthStatusEnum {

        ENABLE(1,"启用"),
        DISABLE(0,"禁用")

        ;

        private final Integer code;
        private final String info;

        GatewayAuthStatusEnum(Integer code, String info) {
            this.code = code;
            this.info = info;
        }

        public static GatewayAuthStatusEnum getByCode(Integer code){
            if(null == code){
                return null;
            }
            for (GatewayAuthStatusEnum anEnum : GatewayAuthStatusEnum.values()) {
                if(anEnum.code.equals(code)){
                    return anEnum;
                }
            }

            throw new AppException(ResponseCode.ENUM_NOT_FOUND.getCode(), ResponseCode.ENUM_NOT_FOUND.getInfo());
        }

    }

}
