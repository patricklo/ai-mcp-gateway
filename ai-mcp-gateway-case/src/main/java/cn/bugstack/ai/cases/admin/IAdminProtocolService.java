package cn.bugstack.ai.cases.admin;

import cn.bugstack.ai.domain.protocol.model.entity.StorageCommandEntity;

/**
 * 协议配置管理
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/24 08:10
 */
public interface IAdminProtocolService {

    void saveGatewayProtocol(StorageCommandEntity commandEntity);

    void deleteGatewayProtocol(Long protocolId);

    void importGatewayProtocol(cn.bugstack.ai.domain.protocol.model.entity.AnalysisCommandEntity commandEntity);

    java.util.List<cn.bugstack.ai.domain.protocol.model.valobj.http.HTTPProtocolVO> analysisProtocol(cn.bugstack.ai.domain.protocol.model.entity.AnalysisCommandEntity commandEntity);

}
