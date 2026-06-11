package cn.bugstack.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网关工具分页实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolPageEntity {

    /** 数据列表 */
    private List<GatewayToolConfigEntity> dataList;

    /** 总条数 */
    private Long total;

}
