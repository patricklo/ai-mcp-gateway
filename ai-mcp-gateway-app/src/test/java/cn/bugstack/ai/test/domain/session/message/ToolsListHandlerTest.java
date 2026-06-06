package cn.bugstack.ai.test.domain.session.message;

import cn.bugstack.ai.domain.session.model.valobj.McpSchemaVO;
import cn.bugstack.ai.domain.session.service.message.handler.IRequestHandler;
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
public class ToolsListHandlerTest {

    @Resource
    private IRequestHandler toolsListHandler;

    @Test
    public void test_handle() {
        McpSchemaVO.JSONRPCResponse handle = toolsListHandler.
                handle("gateway_001",
                        new McpSchemaVO.JSONRPCRequest("2.0","tool/list","a355a5f7-0",""));
        log.info("测试结果:{}", JSON.toJSONString(handle.result()));
    }

}
