package uk.co.whitbread.basket.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HandlerWebhookConfig implements WebMvcConfigurer {

  HandlerInterceptorWebhook handlerInterceptor;

  public HandlerWebhookConfig(HandlerInterceptorWebhook handlerInterceptor) {
    this.handlerInterceptor = handlerInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(handlerInterceptor);
  }
}