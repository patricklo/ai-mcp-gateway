package cn.bugstack.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayProtocolConfigEntity {

    private Long protocolId;
    private String httpUrl;
    private String httpMethod;
    private String httpHeaders;
    private Integer timeout;
    private List<ProtocolMappingEntity> mappings;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProtocolMappingEntity {
        private String mappingType;
        private String parentPath;
        private String fieldName;
        private String mcpPath;
        private String mcpType;
        private String mcpDesc;
        private Integer isRequired;
        private Integer sortOrder;
    }
}
