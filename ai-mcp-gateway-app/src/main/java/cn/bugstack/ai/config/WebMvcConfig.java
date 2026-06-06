package cn.bugstack.ai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        taskExecutor.setMaxPoolSize(threadPoolExecutor.getMaximumPoolSize());
        taskExecutor.setKeepAliveSeconds((int) threadPoolExecutor.getKeepAliveTime(java.util.concurrent.TimeUnit.SECONDS));
        taskExecutor.setQueueCapacity(5000);
        taskExecutor.setThreadNamePrefix("async-mvc-");
        taskExecutor.setRejectedExecutionHandler(threadPoolExecutor.getRejectedExecutionHandler());
        taskExecutor.initialize();
        
        configurer.setTaskExecutor(taskExecutor);
        configurer.setDefaultTimeout(30000L); // 30秒超时
    }

}