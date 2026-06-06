package cn.bugstack.ai.domain.session.model.valobj.enums;

import cn.bugstack.ai.domain.session.model.valobj.McpSchemaVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求方法枚举策略
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/20 09:00
 */
@Slf4j
@Getter
@AllArgsConstructor
public enum SessionMessageHandlerMethodEnum {

    // 根据实际业务需求定义方法类型
    INITIALIZE("initialize", "initializeHandler", "初始化请求"),
    TOOLS_LIST("tools/list", "toolsListHandler", "工具列表请求"),
    TOOLS_CALL("tools/call", "toolsCallHandler", "工具调用请求"),
    RESOURCES_LIST("resources/list", "resourcesListHandler", "资源列表请求"),

    ;

    private final String method;
    private final String handlerName;
    private final String description;

    /**
     * 根据方法名获取枚举
     *
     * @param method 方法名
     * @return 对应的枚举，如果找不到返回UNKNOWN
     */
    public static SessionMessageHandlerMethodEnum getByMethod(String method) {
        for (SessionMessageHandlerMethodEnum value : values()) {
            if (value.getMethod().equals(method)) {
                return value;
            }
        }
        return null;
    }

}