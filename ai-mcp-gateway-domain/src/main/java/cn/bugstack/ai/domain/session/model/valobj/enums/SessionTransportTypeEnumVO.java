package cn.bugstack.ai.domain.session.model.valobj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会话传输协议类型
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/5/25 08:00
 */
@Getter
@AllArgsConstructor
public enum SessionTransportTypeEnumVO {

    SSE("sse", "SSE 传输协议"),
    STREAMABLE("streamable", "Streamable HTTP 传输协议"),

    ;

    private final String code;
    private final String info;

}
