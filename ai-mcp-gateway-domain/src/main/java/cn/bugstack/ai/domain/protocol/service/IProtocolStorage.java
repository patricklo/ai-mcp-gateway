package cn.bugstack.ai.domain.protocol.service;

import cn.bugstack.ai.domain.protocol.model.entity.StorageCommandEntity;

import java.util.List;

/**
 * 协议存储接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/3 07:29
 */
public interface IProtocolStorage {

    List<Long> doStorage(StorageCommandEntity commandEntity);

    void deleteGatewayProtocol(Long protocolId);

}
