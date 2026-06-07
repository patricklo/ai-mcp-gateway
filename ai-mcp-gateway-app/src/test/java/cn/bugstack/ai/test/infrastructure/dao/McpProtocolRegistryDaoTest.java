package cn.bugstack.ai.test.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.bugstack.ai.infrastructure.dao.po.McpProtocolHttpPO;
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
    private IMcpProtocolHttpDao mcpProtocolRegistryDao;

}