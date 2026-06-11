// js/config.js

const API_BASE_URL = "http://127.0.0.1:8777/api-gateway"; // 替换为实际的服务端IP和端口

const API_ENDPOINTS = {
    // 获取网关列表
    GET_GATEWAY_LIST: `${API_BASE_URL}/admin/query_gateway_config_list`,
    GET_GATEWAY_PAGE: `${API_BASE_URL}/admin/query_gateway_config_page`,
    // 保存网关配置
    SAVE_GATEWAY_CONFIG: `${API_BASE_URL}/admin/save_gateway_config`,
    // 保存网关工具配置
    SAVE_GATEWAY_TOOL_CONFIG: `${API_BASE_URL}/admin/save_gateway_tool_config`,
    // 获取网关协议列表
    GET_GATEWAY_PROTOCOL_LIST: `${API_BASE_URL}/admin/query_gateway_protocol_list`,
    GET_GATEWAY_PROTOCOL_PAGE: `${API_BASE_URL}/admin/query_gateway_protocol_page`,
    // 根据网关ID获取协议列表
    GET_GATEWAY_PROTOCOL_LIST_BY_ID: `${API_BASE_URL}/admin/query_gateway_protocol_list_by_gateway_id`,
    // 保存网关协议配置
    SAVE_GATEWAY_PROTOCOL: `${API_BASE_URL}/admin/save_gateway_protocol`,
    // 导入网关协议配置
    IMPORT_GATEWAY_PROTOCOL: `${API_BASE_URL}/admin/import_gateway_protocol`,
    // 解析网关协议配置
    ANALYSIS_PROTOCOL: `${API_BASE_URL}/admin/analysis_protocol`,
    // 删除网关协议配置
    DELETE_GATEWAY_PROTOCOL: `${API_BASE_URL}/admin/delete_gateway_protocol`,
    // 获取网关认证列表
    GET_GATEWAY_AUTH_LIST: `${API_BASE_URL}/admin/query_gateway_auth_list`,
    GET_GATEWAY_AUTH_PAGE: `${API_BASE_URL}/admin/query_gateway_auth_page`,
    // 保存网关认证配置
    SAVE_GATEWAY_AUTH: `${API_BASE_URL}/admin/save_gateway_auth`,
    // 删除网关认证配置
    DELETE_GATEWAY_AUTH: `${API_BASE_URL}/admin/delete_gateway_auth`,
    // 获取网关工具列表
    GET_GATEWAY_TOOL_LIST: `${API_BASE_URL}/admin/query_gateway_tool_list`,
    GET_GATEWAY_TOOL_PAGE: `${API_BASE_URL}/admin/query_gateway_tool_page`,
    // 根据网关ID获取工具列表
    GET_GATEWAY_TOOL_LIST_BY_ID: `${API_BASE_URL}/admin/query_gateway_tool_list_by_gateway_id`,
    // 删除网关工具配置
    DELETE_GATEWAY_TOOL: `${API_BASE_URL}/admin/delete_gateway_tool_config`
};

// 模拟登录账号
const MOCK_ACCOUNT = {
    username: "admin",
    password: "password123"
};
