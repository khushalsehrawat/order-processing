package com.example.coordinator.config;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.time.Duration;
import java.util.concurrent.Executor;
@Configuration
public class AppConfig {
  @Bean RestClient restClient(RestClient.Builder builder,@Value("${coordinator.request.timeout-seconds:5}") int timeoutSeconds){
    SimpleClientHttpRequestFactory requestFactory=new SimpleClientHttpRequestFactory();
    Duration timeout=Duration.ofSeconds(timeoutSeconds);
    requestFactory.setConnectTimeout(timeout);
    requestFactory.setReadTimeout(timeout);
    return builder.requestFactory(requestFactory).build();
  }
  @Bean("orderExecutor")
  @Primary
  Executor orderExecutor(
    @Value("${coordinator.executor.core-pool-size:16}") int corePoolSize,
    @Value("${coordinator.executor.max-pool-size:32}") int maxPoolSize,
    @Value("${coordinator.executor.queue-capacity:1000}") int queueCapacity){
    ThreadPoolTaskExecutor e=new ThreadPoolTaskExecutor();
    e.setCorePoolSize(corePoolSize); e.setMaxPoolSize(maxPoolSize); e.setQueueCapacity(queueCapacity);
    e.setThreadNamePrefix("order-worker-"); e.initialize(); return e;
  }
}
