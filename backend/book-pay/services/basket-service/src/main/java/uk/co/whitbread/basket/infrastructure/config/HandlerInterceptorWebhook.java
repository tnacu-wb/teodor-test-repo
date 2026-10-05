package uk.co.whitbread.basket.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.HttpClientErrorException;

@Configuration
@Data
@ConfigurationProperties(prefix = "whitbread.api")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HandlerInterceptorWebhook implements
    org.springframework.web.servlet.HandlerInterceptor {

  private static final String KEY = "X-WHIT-API-KEY";
  private static final String VALUE = "b246acfc-2271-4c0d-ac39-02554040b499";
  private static final String WEBHOOK_PATH = "/payment-webhook";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws HttpClientErrorException.Unauthorized, IOException {
    if (request.getRequestURI().contains(WEBHOOK_PATH)
        && !request.getHeader(KEY).equals(VALUE)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "Unauthorized exception, wrong API Key and/or Value");
      return false;
    }
    return true;
  }
}