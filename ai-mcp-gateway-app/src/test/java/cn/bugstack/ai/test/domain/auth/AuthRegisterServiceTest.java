package cn.bugstack.ai.test.domain.auth;

import cn.bugstack.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.bugstack.ai.domain.auth.service.IAuthRegisterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 注册服务测试
 *
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthRegisterServiceTest {

    @Resource
    private IAuthRegisterService authRegisterService;

    @Test
    public void test_register() {
        RegisterCommandEntity commandEntity = new RegisterCommandEntity();
        commandEntity.setGatewayId("gateway_001");
        commandEntity.setRateLimit(10);
        // 过期时间：2天
        commandEntity.setExpireTime(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 2));

        String apiKey = authRegisterService.register(commandEntity);
        log.info("注册结果 apiKey: {}", apiKey);
        
        assertNotNull(apiKey);
        assertTrue(apiKey.startsWith("gw-"));
    }

}
