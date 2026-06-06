package cn.bugstack.ai.domain.session.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 会话配置值对象
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/2 07:57
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionConfigVO {

    /**
     * 会话唯一标识符
     */
    private String sessionId;

    /**
     * 流式响应
     */
    private Sinks.Many<ServerSentEvent<String>> sink;

    /**
     * 会话时间
     */
    private Instant createTime;

    /**
     * 最后访问时间戳，volatile 确保多线程下可见性
     */
    private volatile Instant lastAccessedTime;

    /**
     * 会话活跃状态标识
     */
    private volatile boolean active;

    public SessionConfigVO(String sessionId, Sinks.Many<ServerSentEvent<String>> sink) {
        this.sessionId = sessionId;
        this.sink = sink;
        this.createTime = Instant.now();
        this.lastAccessedTime = Instant.now();
        this.active = true;
    }

    /**
     * 标记会话为非活跃状态
     */
    public void markInactive() {
        this.active = false;
    }

    /**
     * 更新最后访问时间
     */
    public void updateLastAccessed() {
        this.lastAccessedTime = Instant.now();
    }

    /**
     * 过期时间判断
     */
    public boolean isExpired(long timeoutMinutes) {
        return lastAccessedTime.isBefore(Instant.now().minus(timeoutMinutes, ChronoUnit.MINUTES));
    }

}
