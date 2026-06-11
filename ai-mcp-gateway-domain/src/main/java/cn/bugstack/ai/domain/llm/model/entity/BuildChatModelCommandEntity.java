package cn.bugstack.ai.domain.llm.model.entity;


import cn.bugstack.ai.domain.llm.model.valobj.McpConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildChatModelCommandEntity {

    private String gatewayId;

    private McpConfigVO mcpConfigVO;
}
