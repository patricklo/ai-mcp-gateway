package cn.bugstack.ai.test.domain.protocol;

import cn.bugstack.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.bugstack.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.bugstack.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.bugstack.ai.domain.protocol.service.IProtocolAnalysis;
import cn.bugstack.ai.domain.protocol.service.IProtocolStorage;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProtocolStorageTest {

    @Value("classpath:swagger/api-docs-test03.json")
    private org.springframework.core.io.Resource apiDocs;

    @Autowired
    private IProtocolAnalysis protocolAnalysis;

    @Resource
    private IProtocolStorage protocolStorage;

    @Test
    public void test_storage() throws IOException {
        // 1. 协议解析
        String json = new String(FileCopyUtils.copyToByteArray(apiDocs.getInputStream()), StandardCharsets.UTF_8);
        List<String> endpoints = Arrays.asList("/api/v1/mcp/get_company_employee");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-test03");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-test02");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-by-id-01");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-by-id-02");
//        List<String> endpoints = Arrays.asList("/api/v1/mcp/query-by-id-03");

        AnalysisCommandEntity commandEntity = AnalysisCommandEntity.builder()
                .openApiJson(json)
                .endpoints(endpoints)
                .build();

        List<HTTPProtocolVO> httpProtocolVOS = protocolAnalysis.doAnalysis(commandEntity);
        log.info("解析协议:{}", JSON.toJSONString(httpProtocolVOS));

        // 2. 协议存储
        List<Long> protocolIdList = protocolStorage.doStorage(
                StorageCommandEntity.builder()
                        .httpProtocolVOS(httpProtocolVOS).build());

        log.info("存储协议:{}", JSON.toJSONString(protocolIdList));
    }

}
