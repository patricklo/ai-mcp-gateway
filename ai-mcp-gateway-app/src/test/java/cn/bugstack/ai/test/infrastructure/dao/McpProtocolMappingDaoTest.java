package cn.bugstack.ai.test.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.bugstack.ai.infrastructure.dao.po.McpProtocolMappingPO;
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
public class McpProtocolMappingDaoTest {

    @Resource
    private IMcpProtocolMappingDao mcpProtocolMappingDao;

    @Test
    public void testInsert() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .gatewayId("")
                .toolId(2L)
                .mappingType("request")
                .parentPath(null)
                .fieldName("testRequest")
                .mcpPath("testRequest")
                .mcpType("object")
                .mcpDesc("测试请求对象")
                .isRequired(1)
                .httpPath(null)
                .httpLocation("body")
                .sortOrder(1)
                .build();

        int result = mcpProtocolMappingDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        assertNotNull("插入后ID不应该为null", po.getId());
        log.info("插入成功，生成的ID: {}", po.getId());
    }

    @Test
    public void testQueryById() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .gatewayId("")
                .toolId(3L)
                .mappingType("response")
                .parentPath("testResponse")
                .fieldName("result")
                .mcpPath("testResponse.result")
                .mcpType("string")
                .mcpDesc("响应结果")
                .isRequired(0)
                .httpPath("data.result")
                .httpLocation("body")
                .sortOrder(1)
                .build();

        mcpProtocolMappingDao.insert(po);
        
        McpProtocolMappingPO result = mcpProtocolMappingDao.queryById(po.getId());
        
        assertNotNull("查询结果不应该为null", result);
        assertEquals("工具ID应该匹配", Long.valueOf(3L), result.getToolId());
        assertEquals("映射类型应该匹配", "response", result.getMappingType());
        assertEquals("字段名称应该匹配", "result", result.getFieldName());
        assertEquals("MCP路径应该匹配", "testResponse.result", result.getMcpPath());
        assertEquals("数据类型应该匹配", "string", result.getMcpType());
        log.info("查询成功: {}", result);
    }

    @Test
    public void testQueryAll() {
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.queryAll();
        
        assertNotNull("查询结果不应该为null", list);
        log.info("查询到 {} 条记录", list.size());
        
        if (!list.isEmpty()) {
            list.forEach(item -> log.info("记录: {}", item));
        }
    }

    @Test
    public void testUpdateById() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .gatewayId("")
                .toolId(4L)
                .mappingType("request")
                .parentPath("updateTest")
                .fieldName("city")
                .mcpPath("updateTest.city")
                .mcpType("string")
                .mcpDesc("城市名称-更新前")
                .isRequired(1)
                .httpPath("city")
                .httpLocation("body")
                .sortOrder(1)
                .build();

        mcpProtocolMappingDao.insert(po);
        
        po.setMcpDesc("城市名称-更新后");
        po.setMcpType("object");
        po.setIsRequired(0);
        
        int updateResult = mcpProtocolMappingDao.updateById(po);
        
        assertEquals("更新操作应该返回1", 1, updateResult);
        
        McpProtocolMappingPO updated = mcpProtocolMappingDao.queryById(po.getId());
        assertEquals("描述应该更新", "城市名称-更新后", updated.getMcpDesc());
        assertEquals("数据类型应该更新", "object", updated.getMcpType());
        assertEquals("是否必填应该更新", Integer.valueOf(0), updated.getIsRequired());
        log.info("更新成功: {}", updated);
    }

    @Test
    public void testDeleteById() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .gatewayId("")
                .toolId(5L)
                .mappingType("request")
                .parentPath(null)
                .fieldName("deleteTest")
                .mcpPath("deleteTest")
                .mcpType("array")
                .mcpDesc("将要删除的测试数据")
                .isRequired(1)
                .httpPath(null)
                .httpLocation("body")
                .sortOrder(1)
                .build();

        mcpProtocolMappingDao.insert(po);
        
        int deleteResult = mcpProtocolMappingDao.deleteById(po.getId());
        
        assertEquals("删除操作应该返回1", 1, deleteResult);
        
        McpProtocolMappingPO deleted = mcpProtocolMappingDao.queryById(po.getId());
        assertNull("删除后查询应该返回null", deleted);
        log.info("删除成功，ID: {}", po.getId());
    }

    @Test
    public void testQueryExistingMappingData() {
        // 测试查询已存在的数据，基于SQL文件中的数据
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.queryAll();
        
        assertNotNull("查询结果不应该为null", list);
        
        // 查找SQL文件中已存在的数据
        boolean foundXxxRequest01 = list.stream()
                .anyMatch(item -> "xxxRequest01".equals(item.getFieldName()) && 
                                "xxxRequest01".equals(item.getMcpPath()) &&
                                "object".equals(item.getMcpType()));
        
        if (foundXxxRequest01) {
            log.info("找到SQL文件中已存在的xxxRequest01记录");
        } else {
            log.info("未找到SQL文件中已存在的xxxRequest01记录，可能数据库为空");
        }
        
        log.info("查询现有数据测试完成，共找到 {} 条记录", list.size());
    }

    @Test
    public void testInsertWithNullParentPath() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .gatewayId("")
                .toolId(6L)
                .mappingType("request")
                .parentPath(null) // 父路径为null
                .fieldName("rootObject")
                .mcpPath("rootObject")
                .mcpType("object")
                .mcpDesc("根对象")
                .isRequired(1)
                .httpPath(null)
                .httpLocation("body")
                .sortOrder(0)
                .build();

        int result = mcpProtocolMappingDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        assertNotNull("插入后ID不应该为null", po.getId());
        
        McpProtocolMappingPO queryResult = mcpProtocolMappingDao.queryById(po.getId());
        assertNull("父路径应该为null", queryResult.getParentPath());
        log.info("插入父路径为null的记录成功");
    }

    @Test
    public void testInsertWithRequiredField() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .gatewayId("")
                .toolId(7L)
                .mappingType("request")
                .parentPath("requiredTest")
                .fieldName("requiredField")
                .mcpPath("requiredTest.requiredField")
                .mcpType("string")
                .mcpDesc("必填字段测试")
                .isRequired(1) // 必填字段
                .httpPath("requiredField")
                .httpLocation("body")
                .sortOrder(1)
                .build();

        int result = mcpProtocolMappingDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        
        McpProtocolMappingPO queryResult = mcpProtocolMappingDao.queryById(po.getId());
        assertEquals("应该为必填字段", Integer.valueOf(1), queryResult.getIsRequired());
        log.info("插入必填字段记录成功");
    }

    @Test
    public void testInsertWithQueryLocation() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .gatewayId("")
                .toolId(8L)
                .mappingType("request")
                .parentPath(null)
                .fieldName("queryParam")
                .mcpPath("queryParam")
                .mcpType("string")
                .mcpDesc("查询参数")
                .isRequired(0)
                .httpPath("param")
                .httpLocation("query") // 查询参数位置
                .sortOrder(1)
                .build();

        int result = mcpProtocolMappingDao.insert(po);
        
        assertEquals("插入操作应该返回1", 1, result);
        
        McpProtocolMappingPO queryResult = mcpProtocolMappingDao.queryById(po.getId());
        assertEquals("HTTP位置应该为query", "query", queryResult.getHttpLocation());
        log.info("插入查询参数位置记录成功");
    }

    @Test
    public void testUpdateSortOrderOnly() {
        McpProtocolMappingPO po = McpProtocolMappingPO.builder()
                .gatewayId("")
                .toolId(9L)
                .mappingType("request")
                .parentPath("sortTest")
                .fieldName("sortField")
                .mcpPath("sortTest.sortField")
                .mcpType("string")
                .mcpDesc("排序测试字段")
                .isRequired(1)
                .httpPath("sortField")
                .httpLocation("body")
                .sortOrder(1)
                .build();

        mcpProtocolMappingDao.insert(po);
        
        // 只更新排序顺序
        po.setSortOrder(5);
        int updateResult = mcpProtocolMappingDao.updateById(po);
        
        assertEquals("更新操作应该返回1", 1, updateResult);
        
        McpProtocolMappingPO updated = mcpProtocolMappingDao.queryById(po.getId());
        assertEquals("排序顺序应该更新为5", Integer.valueOf(5), updated.getSortOrder());
        assertEquals("其他字段应该保持不变", "sortField", updated.getFieldName());
        log.info("排序顺序更新测试成功");
    }
}