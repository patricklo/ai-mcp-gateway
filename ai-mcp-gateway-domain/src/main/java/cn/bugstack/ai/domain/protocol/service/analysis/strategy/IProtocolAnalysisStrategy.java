package cn.bugstack.ai.domain.protocol.service.analysis.strategy;

import cn.bugstack.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 协议解析策略接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 */
public interface IProtocolAnalysisStrategy {

    void doAnalysis(JSONObject operation, JSONObject definitions, List<HTTPProtocolVO.ProtocolMapping> mappings);

}
