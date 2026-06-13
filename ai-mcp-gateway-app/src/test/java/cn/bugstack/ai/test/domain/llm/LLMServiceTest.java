package cn.bugstack.ai.test.domain.llm;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * LLM Test
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/4/8 08:01
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LLMServiceTest {

    @Resource
    private OpenAiApi openAiApi;

    @Test
    public void test() {
        System.out.println("hi！");
    }

}
