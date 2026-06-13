package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.McpGatewayAuthPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMcpGatewayAuthDao {

    int insert(McpGatewayAuthPO po);

    int deleteById(Long id);

    int deleteByGatewayId(String gatewayId);

    int updateById(McpGatewayAuthPO po);

    int updateByGatewayId(McpGatewayAuthPO po);

    McpGatewayAuthPO queryById(Long id);

    List<McpGatewayAuthPO> queryAll();

    List<McpGatewayAuthPO> queryAuthList(McpGatewayAuthPO query);

    Long queryAuthListCount(McpGatewayAuthPO query);

    /**
     * 根据网关ID精确查询该网关下的所有认证记录
     */
    List<McpGatewayAuthPO> queryListByGatewayId(String gatewayId);

    McpGatewayAuthPO queryMcpGatewayAuthPO(McpGatewayAuthPO req);

    int queryEffectiveGatewayAuthCount(String gatewayId);

}

