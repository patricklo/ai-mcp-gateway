package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.McpProtocolRegistryPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMcpProtocolRegistryDao {

    int insert(McpProtocolRegistryPO po);

    int deleteById(Long id);

    int updateById(McpProtocolRegistryPO po);

    McpProtocolRegistryPO queryById(Long id);

    List<McpProtocolRegistryPO> queryAll();

    McpProtocolRegistryPO queryMcpProtocolRegistryByGatewayId(String gatewayId);

}

