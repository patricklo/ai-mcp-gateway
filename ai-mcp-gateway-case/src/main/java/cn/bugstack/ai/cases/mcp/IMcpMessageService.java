package cn.bugstack.ai.cases.mcp;

import cn.bugstack.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 * MCP 消息服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 09:08
 */
public interface IMcpMessageService<T> {

    ResponseEntity<T> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception;

}
