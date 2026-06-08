package cn.bugstack.ai.test.domain.auth;

import cn.bugstack.ai.domain.auth.model.entity.RateLimitCommandEntity;
import cn.bugstack.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.bugstack.ai.domain.auth.service.IAuthRateLimitService;
import cn.bugstack.ai.domain.auth.service.IAuthRegisterService;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 限流服务测试
 *
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthRateLimitServiceTest {

    @Resource
    private IAuthRateLimitService authRateLimitService;

    @Resource
    private IAuthRegisterService authRegisterService;

    @Test
    public void test_rateLimit_success() throws InterruptedException {
        // 1. 注册 (36000次/小时 => 10次/秒)
        /*RegisterCommandEntity registerCommandEntity = new RegisterCommandEntity();
        registerCommandEntity.setGatewayId("gateway_001");
        registerCommandEntity.setRateLimit(36000);
        registerCommandEntity.setExpireTime(new Date(System.currentTimeMillis() + 1000L * 60 * 60)); 
        String apiKey = authRegisterService.register(registerCommandEntity);
        log.info("注册结果 apiKey: {}", apiKey);*/

        // 1. 可以走新注册 apiKey，或者找到数据表中现存的数据测试。
        RateLimitCommandEntity commandEntity = new RateLimitCommandEntity();
        commandEntity.setGatewayId("gateway_001");
        commandEntity.setApiKey("gw-lf3HFzlJCdnrYl20oHbd5lJQxE7GWz8wjsSgjDZfctJNV8s5");

        // 2.1 第一次调用
        boolean rateLimit = authRateLimitService.rateLimit(commandEntity);
        log.info("限流结果(第一次) rateLimit: {}", rateLimit);
        assertFalse(rateLimit); // false 表示未被限流

        // 休息 150ms，让令牌桶生成新的令牌（10次/秒 = 100ms/次）
        Thread.sleep(150);
        
        // 2.2 第二次调用
        rateLimit = authRateLimitService.rateLimit(commandEntity);
        log.info("限流结果(第二次) rateLimit: {}", rateLimit);
        assertFalse(rateLimit);
    }

    @Test
    public void testGuavaRateLimiter() throws InterruptedException {
        RateLimiter rateLimiter = RateLimiter.create(1.0);

        log.info("限流:{}",rateLimiter.tryAcquire());
        log.info("限流:{}",rateLimiter.tryAcquire());

        System.out.println("sleep...");
        Thread.sleep(1000);

        log.info("限流:{}",rateLimiter.tryAcquire());
        log.info("限流:{}",rateLimiter.tryAcquire());
        log.info("限流:{}",rateLimiter.tryAcquire());
    }

}
