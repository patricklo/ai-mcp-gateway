package cn.bugstack.ai.test.trigger.http;

import cn.bugstack.ai.api.dto.GatewayLLMRequestDTO;
import cn.bugstack.ai.api.dto.GatewayLLMResponseDTO;
import cn.bugstack.ai.api.response.Response;
import cn.bugstack.ai.trigger.http.AdminController;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminControllerTest {

    @Resource
    private AdminController adminController;

    @Test
    public void test_testCallGateway() {
        GatewayLLMRequestDTO requestDTO = GatewayLLMRequestDTO.builder()
                .gatewayId("gateway_005")
                .reload(true)
                .authApiKey("gw-GPJBQHFeBWVMSGASFii5xtsmlHF5SjURFwh7C7yGRP3UtX")
                .timeout(3000)
                .message("""
                获取公司雇员信息，信息如下；
                城市；北京
                公司；谷歌
                雇员；小傅哥""")
                .build();

        Response<GatewayLLMResponseDTO> response = adminController.testCallGateway(requestDTO);

        log.info("测试结果:{}", JSON.toJSONString(response));
    }

}
