package cn.bugstack.ai.test.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.IMcpProtocolRegistryDao;
import cn.bugstack.ai.infrastructure.dao.po.McpProtocolRegistryPO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class McpProtocolRegistryDaoTest {

    @Resource
    private IMcpProtocolRegistryDao mcpProtocolRegistryDao;

    @Test
    public void testInsert() {
        McpProtocolRegistryPO po = McpProtocolRegistryPO.builder()
                .gatewayId("gateway_002")
                .toolId(2L)
                .toolName("TestTool_getUserInfo")
                .toolType("function")
                .toolDescription("获取用户信息工具")
                .httpUrl("http://localhost:8701/api/v1/user/info")
                .httpMethod("GET")
                .httpHeaders("{\"Content-Type\": \"application/json\", \"Authorization\": \"Bearer token\"}")
                .timeout(15000)
                .retryTimes(1)
                .status(1)
                .build();

        int result = mcpProtocolRegistryDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        assertNotNull("插入后ID不应该为null", po.getId());
        log.info("插入成功，生成的ID: {}", po.getId());
    }

    @Test
    public void testQueryById() {
        McpProtocolRegistryPO po = McpProtocolRegistryPO.builder()
                .gatewayId("gateway_003")
                .toolId(3L)
                .toolName("TestTool_createOrder")
                .toolType("function")
                .toolDescription("创建订单工具")
                .httpUrl("http://localhost:8701/api/v1/order/create")
                .httpMethod("POST")
                .httpHeaders("{\"Content-Type\": \"application/json\"}")
                .timeout(20000)
                .retryTimes(2)
                .status(1)
                .build();

        mcpProtocolRegistryDao.insert(po);
        
        McpProtocolRegistryPO result = mcpProtocolRegistryDao.queryById(po.getId());
        
        assertNotNull("查询结果不应该为null", result);
        assertEquals("网关ID应该匹配", "gateway_003", result.getGatewayId());
        assertEquals("工具ID应该匹配", Long.valueOf(3L), result.getToolId());
        assertEquals("工具名称应该匹配", "TestTool_createOrder", result.getToolName());
        assertEquals("工具类型应该匹配", "function", result.getToolType());
        assertEquals("HTTP方法应该匹配", "POST", result.getHttpMethod());
        assertEquals("状态应该匹配", Integer.valueOf(1), result.getStatus());
        log.info("查询成功: {}", result);
    }

    @Test
    public void testQueryAll() {
        List<McpProtocolRegistryPO> list = mcpProtocolRegistryDao.queryAll();
        
        assertNotNull("查询结果不应该为null", list);
        log.info("查询到 {} 条记录", list.size());
        
        if (!list.isEmpty()) {
            list.forEach(item -> log.info("记录: {}", item));
        }
    }

    @Test
    public void testUpdateById() {
        McpProtocolRegistryPO po = McpProtocolRegistryPO.builder()
                .gatewayId("gateway_004")
                .toolId(4L)
                .toolName("TestTool_updateData")
                .toolType("function")
                .toolDescription("更新数据工具-更新前")
                .httpUrl("http://localhost:8701/api/v1/data/update")
                .httpMethod("PUT")
                .httpHeaders("{\"Content-Type\": \"application/json\"}")
                .timeout(25000)
                .retryTimes(1)
                .status(1)
                .build();

        mcpProtocolRegistryDao.insert(po);
        
        po.setToolDescription("更新数据工具-更新后");
        po.setHttpMethod("PATCH");
        po.setTimeout(30000);
        po.setRetryTimes(3);
        po.setStatus(0);
        
        int updateResult = mcpProtocolRegistryDao.updateById(po);
        
        assertEquals("更新操作应该返回1", 1, updateResult);
        
        McpProtocolRegistryPO updated = mcpProtocolRegistryDao.queryById(po.getId());
        assertEquals("工具描述应该更新", "更新数据工具-更新后", updated.getToolDescription());
        assertEquals("HTTP方法应该更新", "PATCH", updated.getHttpMethod());
        assertEquals("超时时间应该更新", Integer.valueOf(30000), updated.getTimeout());
        assertEquals("重试次数应该更新", Integer.valueOf(3), updated.getRetryTimes());
        assertEquals("状态应该更新", Integer.valueOf(0), updated.getStatus());
        log.info("更新成功: {}", updated);
    }

    @Test
    public void testDeleteById() {
        McpProtocolRegistryPO po = McpProtocolRegistryPO.builder()
                .gatewayId("gateway_005")
                .toolId(5L)
                .toolName("TestTool_deleteRecord")
                .toolType("function")
                .toolDescription("删除记录工具")
                .httpUrl("http://localhost:8701/api/v1/record/delete")
                .httpMethod("DELETE")
                .httpHeaders("{\"Content-Type\": \"application/json\"}")
                .timeout(10000)
                .retryTimes(0)
                .status(1)
                .build();

        mcpProtocolRegistryDao.insert(po);
        
        int deleteResult = mcpProtocolRegistryDao.deleteById(po.getId());
        
        assertEquals("删除操作应该返回1", 1, deleteResult);
        
        McpProtocolRegistryPO deleted = mcpProtocolRegistryDao.queryById(po.getId());
        assertNull("删除后查询应该返回null", deleted);
        log.info("删除成功，ID: {}", po.getId());
    }

    @Test
    public void testQueryExistingRegistryData() {
        // 测试查询已存在的数据，基于SQL文件中的数据
        List<McpProtocolRegistryPO> list = mcpProtocolRegistryDao.queryAll();
        
        assertNotNull("查询结果不应该为null", list);
        
        // 查找SQL文件中已存在的数据
        boolean foundJavaSDKTool = list.stream()
                .anyMatch(item -> "JavaSDKMCPClient_getCompanyEmployee".equals(item.getToolName()) && 
                                "gateway_001".equals(item.getGatewayId()) &&
                                "function".equals(item.getToolType()));
        
        if (foundJavaSDKTool) {
            log.info("找到SQL文件中已存在的JavaSDKMCPClient_getCompanyEmployee记录");
        } else {
            log.info("未找到SQL文件中已存在的JavaSDKMCPClient_getCompanyEmployee记录，可能数据库为空");
        }
        
        log.info("查询现有数据测试完成，共找到 {} 条记录", list.size());
    }

    @Test
    public void testInsertWithResourceType() {
        McpProtocolRegistryPO po = McpProtocolRegistryPO.builder()
                .gatewayId("gateway_006")
                .toolId(6L)
                .toolName("TestResource_userData")
                .toolType("resource") // 资源类型
                .toolDescription("用户数据资源")
                .httpUrl("http://localhost:8701/api/v1/resource/user")
                .httpMethod("GET")
                .httpHeaders("{\"Content-Type\": \"application/json\"}")
                .timeout(20000)
                .retryTimes(1)
                .status(1)
                .build();

        int result = mcpProtocolRegistryDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        assertNotNull("插入后ID不应该为null", po.getId());
        
        McpProtocolRegistryPO queryResult = mcpProtocolRegistryDao.queryById(po.getId());
        assertEquals("工具类型应该为resource", "resource", queryResult.getToolType());
        log.info("插入资源类型工具记录成功");
    }

    @Test
    public void testInsertWithNullDescription() {
        McpProtocolRegistryPO po = McpProtocolRegistryPO.builder()
                .gatewayId("gateway_007")
                .toolId(7L)
                .toolName("TestTool_noDesc")
                .toolType("function")
                .toolDescription(null) // 描述为null
                .httpUrl("http://localhost:8701/api/v1/test/no-desc")
                .httpMethod("GET")
                .httpHeaders("{\"Content-Type\": \"application/json\"}")
                .timeout(15000)
                .retryTimes(0)
                .status(1)
                .build();

        int result = mcpProtocolRegistryDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        assertNotNull("插入后ID不应该为null", po.getId());
        
        McpProtocolRegistryPO queryResult = mcpProtocolRegistryDao.queryById(po.getId());
        assertNull("描述应该为null", queryResult.getToolDescription());
        log.info("插入描述为null的记录成功");
    }

    @Test
    public void testInsertWithZeroStatus() {
        McpProtocolRegistryPO po = McpProtocolRegistryPO.builder()
                .gatewayId("gateway_008")
                .toolId(8L)
                .toolName("TestTool_disabled")
                .toolType("function")
                .toolDescription("禁用的工具")
                .httpUrl("http://localhost:8701/api/v1/disabled/tool")
                .httpMethod("POST")
                .httpHeaders("{\"Content-Type\": \"application/json\"}")
                .timeout(10000)
                .retryTimes(0)
                .status(0) // 状态为禁用
                .build();

        int result = mcpProtocolRegistryDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        
        McpProtocolRegistryPO queryResult = mcpProtocolRegistryDao.queryById(po.getId());
        assertEquals("状态应该为禁用", Integer.valueOf(0), queryResult.getStatus());
        log.info("插入禁用状态的工具记录成功");
    }

    @Test
    public void testUpdateRetryTimesOnly() {
        McpProtocolRegistryPO po = McpProtocolRegistryPO.builder()
                .gatewayId("gateway_009")
                .toolId(9L)
                .toolName("TestTool_retryUpdate")
                .toolType("function")
                .toolDescription("重试次数更新测试工具")
                .httpUrl("http://localhost:8701/api/v1/retry/test")
                .httpMethod("GET")
                .httpHeaders("{\"Content-Type\": \"application/json\"}")
                .timeout(20000)
                .retryTimes(1)
                .status(1)
                .build();

        mcpProtocolRegistryDao.insert(po);
        
        // 只更新重试次数
        po.setRetryTimes(5);
        int updateResult = mcpProtocolRegistryDao.updateById(po);
        
        assertEquals("更新操作应该返回1", 1, updateResult);
        
        McpProtocolRegistryPO updated = mcpProtocolRegistryDao.queryById(po.getId());
        assertEquals("重试次数应该更新为5", Integer.valueOf(5), updated.getRetryTimes());
        assertEquals("其他字段应该保持不变", "TestTool_retryUpdate", updated.getToolName());
        log.info("重试次数更新测试成功");
    }

    @Test
    public void testInsertWithDifferentHttpMethods() {
        // 测试不同的HTTP方法
        String[] httpMethods = {"GET", "POST", "PUT", "DELETE"};
        
        for (int i = 0; i < httpMethods.length; i++) {
            McpProtocolRegistryPO po = McpProtocolRegistryPO.builder()
                    .gatewayId("gateway_http_" + i)
                    .toolId(10L + i)
                    .toolName("TestTool_" + httpMethods[i] + "Method")
                    .toolType("function")
                    .toolDescription("测试" + httpMethods[i] + "方法的工具")
                    .httpUrl("http://localhost:8701/api/v1/test/" + httpMethods[i].toLowerCase())
                    .httpMethod(httpMethods[i])
                    .httpHeaders("{\"Content-Type\": \"application/json\"}")
                    .timeout(15000)
                    .retryTimes(0)
                    .status(1)
                    .build();

            int result = mcpProtocolRegistryDao.insert(po);
            assertEquals("插入操作应该返回1", 1, result);
            
            McpProtocolRegistryPO queryResult = mcpProtocolRegistryDao.queryById(po.getId());
            assertEquals("HTTP方法应该匹配", httpMethods[i], queryResult.getHttpMethod());
            log.info("插入HTTP方法为{}的工具记录成功", httpMethods[i]);
        }
    }
}