package cn.bugstack.ai.test.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.IMcpGatewayDao;
import cn.bugstack.ai.infrastructure.dao.po.McpGatewayPO;
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
public class McpGatewayDaoTest {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Test
    public void testInsert() {
        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId("gateway_test_001")
                .gatewayName("测试网关001")
                .gatewayDesc("这是一个测试网关的描述信息")
                .status(1)
                .build();

        int result = mcpGatewayDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        assertNotNull("插入后ID不应该为null", po.getId());
        log.info("插入成功，生成的ID: {}", po.getId());
    }

    @Test
    public void testQueryById() {
        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId("gateway_test_002")
                .gatewayName("测试网关002")
                .gatewayDesc("这是另一个测试网关的描述信息")
                .status(1)
                .build();

        mcpGatewayDao.insert(po);
        
        McpGatewayPO result = mcpGatewayDao.queryById(po.getId());
        
        assertNotNull("查询结果不应该为null", result);
        assertEquals("网关ID应该匹配", "gateway_test_002", result.getGatewayId());
        assertEquals("网关名称应该匹配", "测试网关002", result.getGatewayName());
        assertEquals("网关描述应该匹配", "这是另一个测试网关的描述信息", result.getGatewayDesc());
        assertEquals("状态应该匹配", Integer.valueOf(1), result.getStatus());
        log.info("查询成功: {}", result);
    }

    @Test
    public void testQueryAll() {
        List<McpGatewayPO> list = mcpGatewayDao.queryAll();
        
        assertNotNull("查询结果不应该为null", list);
        log.info("查询到 {} 条记录", list.size());
        
        if (!list.isEmpty()) {
            list.forEach(item -> log.info("记录: {}", item));
        }
    }

    @Test
    public void testUpdateById() {
        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId("gateway_test_003")
                .gatewayName("测试网关003")
                .gatewayDesc("这是更新前的描述信息")
                .status(1)
                .build();

        mcpGatewayDao.insert(po);
        
        po.setGatewayName("测试网关003-已更新");
        po.setGatewayDesc("这是更新后的描述信息");
        po.setStatus(0);
        
        int updateResult = mcpGatewayDao.updateById(po);
        
        assertEquals("更新操作应该返回1", 1, updateResult);
        
        McpGatewayPO updated = mcpGatewayDao.queryById(po.getId());
        assertEquals("网关名称应该更新", "测试网关003-已更新", updated.getGatewayName());
        assertEquals("网关描述应该更新", "这是更新后的描述信息", updated.getGatewayDesc());
        assertEquals("状态应该更新", Integer.valueOf(0), updated.getStatus());
        log.info("更新成功: {}", updated);
    }

    @Test
    public void testDeleteById() {
        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId("gateway_test_004")
                .gatewayName("测试网关004")
                .gatewayDesc("这是将要删除的网关")
                .status(1)
                .build();

        mcpGatewayDao.insert(po);
        
        int deleteResult = mcpGatewayDao.deleteById(po.getId());
        
        assertEquals("删除操作应该返回1", 1, deleteResult);
        
        McpGatewayPO deleted = mcpGatewayDao.queryById(po.getId());
        assertNull("删除后查询应该返回null", deleted);
        log.info("删除成功，ID: {}", po.getId());
    }

    @Test
    public void testQueryExistingGatewayData() {
        // 测试查询已存在的数据，基于SQL文件中的数据
        List<McpGatewayPO> list = mcpGatewayDao.queryAll();
        
        assertNotNull("查询结果不应该为null", list);
        
        // 查找SQL文件中已存在的数据
        boolean foundGateway001 = list.stream()
                .anyMatch(item -> "gateway_001".equals(item.getGatewayId()) && 
                                "员工信息查询网关".equals(item.getGatewayName()));
        
        if (foundGateway001) {
            log.info("找到SQL文件中已存在的gateway_001记录");
        } else {
            log.info("未找到SQL文件中已存在的gateway_001记录，可能数据库为空");
        }
        
        log.info("查询现有数据测试完成，共找到 {} 条记录", list.size());
    }

    @Test
    public void testInsertWithNullDescription() {
        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId("gateway_test_null_desc")
                .gatewayName("测试网关无描述")
                .gatewayDesc(null) // 描述为null
                .status(1)
                .build();

        int result = mcpGatewayDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        assertNotNull("插入后ID不应该为null", po.getId());
        
        McpGatewayPO queryResult = mcpGatewayDao.queryById(po.getId());
        assertNull("描述应该为null", queryResult.getGatewayDesc());
        log.info("插入描述为null的记录成功");
    }

    @Test
    public void testInsertWithZeroStatus() {
        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId("gateway_test_zero_status")
                .gatewayName("测试禁用网关")
                .gatewayDesc("这是一个禁用的网关")
                .status(0) // 状态为禁用
                .build();

        int result = mcpGatewayDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        
        McpGatewayPO queryResult = mcpGatewayDao.queryById(po.getId());
        assertEquals("状态应该为禁用", Integer.valueOf(0), queryResult.getStatus());
        log.info("插入禁用状态的网关成功");
    }

    @Test
    public void testUpdateStatusOnly() {
        McpGatewayPO po = McpGatewayPO.builder()
                .gatewayId("gateway_test_status_update")
                .gatewayName("状态测试网关")
                .gatewayDesc("用于测试状态更新")
                .status(1)
                .build();

        mcpGatewayDao.insert(po);
        
        // 只更新状态
        po.setStatus(0);
        int updateResult = mcpGatewayDao.updateById(po);
        
        assertEquals("更新操作应该返回1", 1, updateResult);
        
        McpGatewayPO updated = mcpGatewayDao.queryById(po.getId());
        assertEquals("状态应该更新为禁用", Integer.valueOf(0), updated.getStatus());
        assertEquals("其他字段应该保持不变", "状态测试网关", updated.getGatewayName());
        log.info("状态更新测试成功");
    }
}