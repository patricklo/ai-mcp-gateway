package cn.bugstack.ai.domain.llm.service;

import cn.bugstack.ai.domain.llm.model.entity.BuildChatModelCommandEntity;
import org.springframework.ai.chat.model.ChatModel;

public interface ILLMService {

    void buildChatModel(BuildChatModelCommandEntity commandEntity);

    ChatModel getChatModel(String gatewayId);
}
