package com.example.notification.config;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import java.time.Duration;
@Configuration public class Config {
 @Bean RestClient restClient(RestClient.Builder builder,@Value("${notification.request.timeout-seconds:5}") int timeoutSeconds){
  SimpleClientHttpRequestFactory requestFactory=new SimpleClientHttpRequestFactory();
  Duration timeout=Duration.ofSeconds(timeoutSeconds);
  requestFactory.setConnectTimeout(timeout);
  requestFactory.setReadTimeout(timeout);
  return builder.requestFactory(requestFactory).build();
 }
}
