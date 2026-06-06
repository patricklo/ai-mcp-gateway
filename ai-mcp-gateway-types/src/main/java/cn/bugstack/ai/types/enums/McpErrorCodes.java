package cn.bugstack.ai.types.enums;

/**
 * MCP错误代码常量定义
 * 符合JSON-RPC 2.0规范
 */
public final class McpErrorCodes {
    
    // JSON-RPC 2.0 标准错误代码
    
    /**
     * 解析错误 - 无效的JSON
     */
    public static final int PARSE_ERROR = -32700;
    
    /**
     * 无效请求 - JSON不是有效的请求对象
     */
    public static final int INVALID_REQUEST = -32600;
    
    /**
     * 方法未找到 - 方法不存在或不可用
     */
    public static final int METHOD_NOT_FOUND = -32601;
    
    /**
     * 无效参数 - 无效的方法参数
     */
    public static final int INVALID_PARAMS = -32602;
    
    /**
     * 内部错误 - 内部JSON-RPC错误
     */
    public static final int INTERNAL_ERROR = -32603;
    
    // MCP特定错误代码（-32000到-32099为服务器实现定义的错误）
    
    /**
     * 会话未找到
     */
    public static final int SESSION_NOT_FOUND = -32000;
    
    /**
     * 会话已过期
     */
    public static final int SESSION_EXPIRED = -32001;
    
    /**
     * 服务器正在关闭
     */
    public static final int SERVER_SHUTTING_DOWN = -32002;
    
    /**
     * 工具未找到
     */
    public static final int TOOL_NOT_FOUND = -32003;
    
    /**
     * 工具执行失败
     */
    public static final int TOOL_EXECUTION_FAILED = -32004;
    
    /**
     * 资源未找到
     */
    public static final int RESOURCE_NOT_FOUND = -32005;
    
    /**
     * 权限不足
     */
    public static final int INSUFFICIENT_PERMISSIONS = -32006;
    
    /**
     * 协议版本不支持
     */
    public static final int UNSUPPORTED_PROTOCOL_VERSION = -32007;
    
    private McpErrorCodes() {
        // 工具类，禁止实例化
    }
}