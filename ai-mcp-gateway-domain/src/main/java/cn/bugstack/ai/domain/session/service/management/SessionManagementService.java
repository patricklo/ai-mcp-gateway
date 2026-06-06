package cn.bugstack.ai.domain.session.service.management;

import cn.bugstack.ai.domain.session.model.valobj.SessionConfigVO;
import cn.bugstack.ai.domain.session.service.ISessionManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 会话管理服务
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/2 07:53
 */
@Slf4j
@Service
public class SessionManagementService implements ISessionManagementService {

    /**
     * 会话超时时间（分钟）- 也可以把配置抽取到yml里
     */
    private static final long SESSION_TIMEOUT_MINUTES = 30;

    /**
     * 定时任务调度
     */
    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * 活跃回话存储器，key->sessionId，ConcurrentHashMap 确保线程安全
     */
    private final Map<String, SessionConfigVO> activeSessions = new ConcurrentHashMap<>();

    public SessionManagementService() {
        cleanupScheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 5, 5, TimeUnit.MINUTES);
        log.info("会话管理服务已启动，会话超时时间: {} 分钟", SESSION_TIMEOUT_MINUTES);
    }

    @Override
    public SessionConfigVO createSession(String gatewayId) {
        log.info("创建会话 gatewayId:{}", gatewayId);

        String sessionId = UUID.randomUUID().toString();

        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();

        // 发送端点消息 - 告知客户端消息请求地址（客户端第二次会使用 messageEndpoint 进行请求会话）
        String messageEndpoint = "/api-gateway/" + gatewayId + "/mcp/sse?sessionId=" + sessionId;
        sink.tryEmitNext(ServerSentEvent.<String>builder()
                .event("endpoint")
                .data(messageEndpoint)
                .build());

        SessionConfigVO sessionConfigVO = new SessionConfigVO(sessionId, sink);

        activeSessions.put(sessionId, sessionConfigVO);

        log.info("创建会话 gatewayId:{} sessionId:{},当前活跃会话数:{}", gatewayId, sessionId, activeSessions.size());

        return sessionConfigVO;
    }

    @Override
    public void removeSession(String sessionId) {
        log.info("删除会话配置 sessionId:{}", sessionId);
        SessionConfigVO sessionConfigVO = activeSessions.remove(sessionId);
        if (null == sessionConfigVO) return;

        sessionConfigVO.markInactive();

        try {
            sessionConfigVO.getSink().tryEmitComplete();
        } catch (Exception e) {
            log.warn("关闭会话Sink时出错:{}", e.getMessage());
        }

        log.info("移除会话:{},剩余活跃会话数:{}", sessionId, activeSessions.size());
    }

    @Override
    public SessionConfigVO getSession(String sessionId) {
        if (null == sessionId || sessionId.isEmpty()) {
            return null;
        }

        SessionConfigVO sessionConfigVO = activeSessions.get(sessionId);
        if (null != sessionConfigVO && sessionConfigVO.isActive()) {
            sessionConfigVO.updateLastAccessed();
            return sessionConfigVO;
        }

        return null;
    }

    public void cleanupExpiredSessions() {
        int cleanedCount = 0;

        for (Map.Entry<String, SessionConfigVO> entry : activeSessions.entrySet()) {
            SessionConfigVO sessionConfigVO = entry.getValue();

            if (!sessionConfigVO.isActive() || sessionConfigVO.isExpired(SESSION_TIMEOUT_MINUTES)) {
                removeSession(sessionConfigVO.getSessionId());
                cleanedCount++;
            }

        }

        // 记录清理日志
        if (cleanedCount > 0) {
            log.info("清理了 {} 个过期会话，剩余活跃会话数: {}", cleanedCount, activeSessions.size());
        }
    }

    @Override
    public void shutdown() {
        log.info("关闭会话管理服务...");

        for (String sessionId : activeSessions.keySet()) {
            removeSession(sessionId);
        }

        // 关闭清理调度器
        cleanupScheduler.shutdown();

        try {
            // 等待5秒让正在执行的任务完成
            if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                // 超时强制关闭
                cleanupScheduler.shutdown();
            }
        } catch (InterruptedException e) {
            // 异常强制关闭
            cleanupScheduler.shutdown();
            Thread.currentThread().interrupt();
        }

        log.info("关闭会话管理服务完成");
    }

}
