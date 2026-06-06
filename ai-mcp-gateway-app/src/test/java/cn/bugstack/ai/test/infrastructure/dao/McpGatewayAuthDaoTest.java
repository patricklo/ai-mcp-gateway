package cn.bugstack.ai.test.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.IMcpGatewayAuthDao;
import cn.bugstack.ai.infrastructure.dao.po.McpGatewayAuthPO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class McpGatewayAuthDaoTest {

    @Resource
    private IMcpGatewayAuthDao mcpGatewayAuthDao;

    @Test
    public void testInsert() {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId("gateway_test_001")
                .apiKey("TEST_API_KEY_123456789")
                .rateLimit(500)
                .expireTime(new Date(System.currentTimeMillis() + 86400000)) // 24小时后过期
                .status(1)
                .build();

        int result = mcpGatewayAuthDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        assertNotNull("插入后ID不应该为null", po.getId());
        log.info("插入成功，生成的ID: {}", po.getId());
    }

    @Test
    public void testQueryById() {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId("gateway_test_002")
                .apiKey("TEST_API_KEY_987654321")
                .rateLimit(800)
                .expireTime(new Date(System.currentTimeMillis() + 172800000)) // 48小时后过期
                .status(1)
                .build();

        mcpGatewayAuthDao.insert(po);
        
        McpGatewayAuthPO result = mcpGatewayAuthDao.queryById(po.getId());
        
        assertNotNull("查询结果不应该为null", result);
        assertEquals("网关ID应该匹配", "gateway_test_002", result.getGatewayId());
        assertEquals("API密钥应该匹配", "TEST_API_KEY_987654321", result.getApiKey());
        assertEquals("速率限制应该匹配", Integer.valueOf(800), result.getRateLimit());
        assertEquals("状态应该匹配", Integer.valueOf(1), result.getStatus());
        log.info("查询成功: {}", result);
    }

    @Test
    public void testQueryAll() {
        List<McpGatewayAuthPO> list = mcpGatewayAuthDao.queryAll();
        
        assertNotNull("查询结果不应该为null", list);
        log.info("查询到 {} 条记录", list.size());
        
        if (!list.isEmpty()) {
            list.forEach(item -> log.info("记录: {}", item));
        }
    }

    @Test
    public void testUpdateById() {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId("gateway_test_003")
                .apiKey("TEST_API_KEY_UPDATE_BEFORE")
                .rateLimit(600)
                .expireTime(new Date(System.currentTimeMillis() + 86400000))
                .status(1)
                .build();

        mcpGatewayAuthDao.insert(po);
        
        po.setApiKey("TEST_API_KEY_UPDATE_AFTER");
        po.setRateLimit(1200);
        po.setStatus(0);
        
        int updateResult = mcpGatewayAuthDao.updateById(po);
        
        assertEquals("更新操作应该返回1", 1, updateResult);
        
        McpGatewayAuthPO updated = mcpGatewayAuthDao.queryById(po.getId());
        assertEquals("API密钥应该更新", "TEST_API_KEY_UPDATE_AFTER", updated.getApiKey());
        assertEquals("速率限制应该更新", Integer.valueOf(1200), updated.getRateLimit());
        assertEquals("状态应该更新", Integer.valueOf(0), updated.getStatus());
        log.info("更新成功: {}", updated);
    }

    @Test
    public void testDeleteById() {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId("gateway_test_004")
                .apiKey("TEST_API_KEY_DELETE")
                .rateLimit(300)
                .expireTime(new Date(System.currentTimeMillis() + 86400000))
                .status(1)
                .build();

        mcpGatewayAuthDao.insert(po);
        
        int deleteResult = mcpGatewayAuthDao.deleteById(po.getId());
        
        assertEquals("删除操作应该返回1", 1, deleteResult);
        
        McpGatewayAuthPO deleted = mcpGatewayAuthDao.queryById(po.getId());
        assertNull("删除后查询应该返回null", deleted);
        log.info("删除成功，ID: {}", po.getId());
    }

    @Test
    public void testQueryByGatewayId() {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId("gateway_001")
                .apiKey("RS590LKPOD8877DDLMFKS4")
                .rateLimit(1000)
                .expireTime(new Date(1893459859000L)) // 2029-01-02 16:44:19
                .status(1)
                .build();

        mcpGatewayAuthDao.insert(po);
        
        List<McpGatewayAuthPO> result = mcpGatewayAuthDao.queryAll();
        
        assertNotNull("查询结果不应该为null", result);
        boolean found = result.stream()
                .anyMatch(item -> "gateway_001".equals(item.getGatewayId()));
        assertTrue("应该能找到gateway_001的记录", found);
        log.info("通过网关ID查询测试完成");
    }

    @Test
    public void testInsertWithNullExpireTime() {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId("gateway_test_null_expire")
                .apiKey("TEST_API_KEY_NULL_EXPIRE")
                .rateLimit(100)
                .expireTime(null) // 过期时间为null
                .status(1)
                .build();

        int result = mcpGatewayAuthDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        assertNotNull("插入后ID不应该为null", po.getId());
        
        McpGatewayAuthPO queryResult = mcpGatewayAuthDao.queryById(po.getId());
        assertNull("过期时间应该为null", queryResult.getExpireTime());
        log.info("插入过期时间为null的记录成功");
    }

    @Test
    public void testInsertWithZeroRateLimit() {
        McpGatewayAuthPO po = McpGatewayAuthPO.builder()
                .gatewayId("gateway_test_zero_rate")
                .apiKey("TEST_API_KEY_ZERO_RATE")
                .rateLimit(0) // 速率限制为0
                .expireTime(new Date(System.currentTimeMillis() + 86400000))
                .status(1)
                .build();

        int result = mcpGatewayAuthDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        
        McpGatewayAuthPO queryResult = mcpGatewayAuthDao.queryById(po.getId());
        assertEquals("速率限制应该为0", Integer.valueOf(0), queryResult.getRateLimit());
        log.info("插入速率限制为0的记录成功");
    }
}