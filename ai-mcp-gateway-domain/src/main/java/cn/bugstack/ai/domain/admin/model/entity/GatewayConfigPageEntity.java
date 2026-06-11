package cn.bugstack.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网关配置分页实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigPageEntity {

    /**
     * 数据列表
     */
    private List<GatewayConfigEntity> dataList;

    /**
     * 总条数
     */
    private Long total;

}
