package cn.bugstack.ai.domain.protocol.service;

import cn.bugstack.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.bugstack.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;

import java.util.List;

/**
 * 协议解析接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/3/3 07:29
 */
public interface IProtocolAnalysis {

    List<HTTPProtocolVO> doAnalysis(AnalysisCommandEntity commandEntity);

}
