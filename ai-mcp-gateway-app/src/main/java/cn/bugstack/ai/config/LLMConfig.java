package cn.bugstack.ai.config;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LLM 配置
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/4/8 08:04
 */
@Configuration
public class LLMConfig {

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    @Bean
    public OpenAiApi openAiApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

}
