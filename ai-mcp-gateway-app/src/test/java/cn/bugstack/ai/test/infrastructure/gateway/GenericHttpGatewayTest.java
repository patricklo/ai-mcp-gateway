package cn.bugstack.ai.test.infrastructure.gateway;

import cn.bugstack.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.bugstack.ai.infrastructure.dao.po.McpProtocolHttpPO;
import cn.bugstack.ai.infrastructure.gateway.GenericHttpGateway;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class GenericHttpGatewayTest {

    @Resource
    private GenericHttpGateway gateway;

    @javax.annotation.Resource
    private IMcpProtocolHttpDao mcpProtocolRegistryDao;

    @Test
    public void test_post() throws Exception {
        McpProtocolHttpPO mcpProtocolHttpPO = mcpProtocolRegistryDao.queryById(1L);

        String httpUrl = mcpProtocolHttpPO.getHttpUrl();
        String httpHeaders = mcpProtocolHttpPO.getHttpHeaders();
        Integer timeout = mcpProtocolHttpPO.getTimeout();

        // 1. 请求参数
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("city", "beijing");

        Map<String, Object> company = new java.util.HashMap<>();
        company.put("name", "alibaba");
        company.put("type", "internet");
        params.put("company", company);

        // 2. 构建请求头
        Map<String, Object> headers = new java.util.HashMap<>();
        headers.put("Content-Type", "application/json");

        // 3. 构建请求体
        RequestBody requestBody = RequestBody.create(JSON.toJSONString(params), MediaType.parse("application/json"));

        // 4. 执行请求
        retrofit2.Call<ResponseBody> call = gateway.post(httpUrl, headers, requestBody);
        ResponseBody responseBody = call.execute().body();

        log.info("测试结果：{}", responseBody != null ? responseBody.string() : null);
    }

}
