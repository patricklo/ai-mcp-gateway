package cn.bugstack.ai.infrastructure.adapter.repository;

import cn.bugstack.ai.domain.protocol.adapter.repository.IProtocolRepository;
import cn.bugstack.ai.domain.protocol.model.valobj.enums.ProtocolStatusEnum;
import cn.bugstack.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.bugstack.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.bugstack.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.bugstack.ai.infrastructure.dao.po.McpProtocolHttpPO;
import cn.bugstack.ai.infrastructure.dao.po.McpProtocolMappingPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 协议仓储服务
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/13 08:22
 */
@Slf4j
@Repository
public class ProtocolRepository implements IProtocolRepository {

    @Resource
    private IMcpProtocolHttpDao protocolHttpDao;

    @Resource
    private IMcpProtocolMappingDao protocolMappingDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Long> saveHttpProtocolAndMapping(List<HTTPProtocolVO> httpProtocolVOS) {
        List<Long> protocolIdList = new ArrayList<>();

        for (HTTPProtocolVO httpProtocolVO : httpProtocolVOS) {

            Long protocolId = httpProtocolVO.getProtocolId();
            boolean isUpdate = false;
            
            if (protocolId != null) {
                isUpdate = true;
            } else {
                // 0. 生成协议ID，八位数字的。
                protocolId = Long.parseLong(RandomStringUtils.randomNumeric(8));
            }

            // 1. 保存/更新 HTTP 协议配置
            McpProtocolHttpPO mcpProtocolHttpPO = McpProtocolHttpPO.builder()
                    .protocolId(protocolId)
                    .httpUrl(httpProtocolVO.getHttpUrl())
                    .httpMethod(httpProtocolVO.getHttpMethod())
                    .httpHeaders(httpProtocolVO.getHttpHeaders())
                    .timeout(httpProtocolVO.getTimeout())
                    .retryTimes(3)
                    .status(ProtocolStatusEnum.ENABLE.getCode())
                    .build();
            
            if (isUpdate) {
                protocolHttpDao.updateByProtocolId(mcpProtocolHttpPO);
                protocolMappingDao.deleteByProtocolId(protocolId);
            } else {
                protocolHttpDao.insert(mcpProtocolHttpPO);
            }

            // 2. 保存协议映射配置
            List<HTTPProtocolVO.ProtocolMapping> mappings = httpProtocolVO.getMappings();
            if (null == mappings || mappings.isEmpty()) {
                protocolIdList.add(protocolId);
                continue;
            }

            for (HTTPProtocolVO.ProtocolMapping mapping : mappings) {
                McpProtocolMappingPO mcpProtocolMappingPO = McpProtocolMappingPO.builder()
                        .protocolId(protocolId)
                        .mappingType(mapping.getMappingType())
                        .parentPath(mapping.getParentPath())
                        .fieldName(mapping.getFieldName())
                        .mcpPath(mapping.getMcpPath())
                        .mcpType(mapping.getMcpType())
                        .mcpDesc(mapping.getMcpDesc())
                        .isRequired(mapping.getIsRequired())
                        .sortOrder(mapping.getSortOrder())
                        .build();
                protocolMappingDao.insert(mcpProtocolMappingPO);
            }

            protocolIdList.add(protocolId);
        }

        return protocolIdList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteGatewayProtocol(Long protocolId) {
        protocolHttpDao.deleteByProtocolId(protocolId);
        protocolMappingDao.deleteByProtocolId(protocolId);
    }

}
