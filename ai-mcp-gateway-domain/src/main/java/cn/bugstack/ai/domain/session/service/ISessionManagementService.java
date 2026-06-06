package cn.bugstack.ai.domain.session.service;

import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;

/**
 * 会话管理服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/2 07:51
 */
public interface ISessionManagementService {

    /**
     * 创建回话
     * @return 会话配置
     */
    SessionConfigVO createSession(String gatewayId);

    /**
     * 删除回话
     * @param sessionId 会话ID
     */
    void removeSession(String sessionId);

    /**
     * 获取会话
     * @param sessionId 会话ID
     * @return 会话配置
     */
    SessionConfigVO getSession(String sessionId);

    /**
     * 清理过期会话
     */
    void cleanupExpiredSessions();

    /**
     * 关闭服务时，清理资源使用
     */
    void shutdown();
}
