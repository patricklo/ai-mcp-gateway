package cn.bugstack.ai.cases.mcp;

import cn.bugstack.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/12/13 09:08
 */
public interface IMcpMessageService {

    ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception;

}
