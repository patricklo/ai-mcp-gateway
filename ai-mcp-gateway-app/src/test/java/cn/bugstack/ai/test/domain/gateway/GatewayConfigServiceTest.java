package cn.bugstack.ai.test.domain.gateway;

import cn.bugstack.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.bugstack.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.bugstack.ai.domain.gateway.service.IGatewayConfigService;
import cn.bugstack.ai.types.enums.GatewayEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 网关配置服务测试
 *
 * @author xiaofuge bugstack.cn @小傅哥
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GatewayConfigServiceTest {

    @Resource
    private IGatewayConfigService gatewayConfigService;

    @Test
    public void test_saveGatewayConfig() {
        GatewayConfigCommandEntity commandEntity = new GatewayConfigCommandEntity();
        GatewayConfigVO gatewayConfigVO = GatewayConfigVO.builder()
                .gatewayId("gateway_003")
                .gatewayName("员工信息查询网关")
                .gatewayDesc("用于查询公司员工信息的MCP网关")
                .version("1.0.0")
                .auth(GatewayEnum.GatewayAuthStatusEnum.ENABLE)
                .status(GatewayEnum.GatewayStatus.STRONG_VERIFIED)
                .build();
        commandEntity.setGatewayConfigVO(gatewayConfigVO);

        gatewayConfigService.saveGatewayConfig(commandEntity);
        log.info("保存网关配置成功 gatewayId: {}", gatewayConfigVO.getGatewayId());
    }

    @Test
    public void test_updateGatewayAuthStatus() {
        GatewayConfigCommandEntity commandEntity = GatewayConfigCommandEntity.buildUpdateGatewayAuthStatusVO("gateway_003", GatewayEnum.GatewayAuthStatusEnum.DISABLE);
        gatewayConfigService.updateGatewayAuthStatus(commandEntity);
        log.info("更新网关鉴权状态成功 gatewayId: {}", commandEntity.getGatewayConfigVO().getGatewayId());
    }

}