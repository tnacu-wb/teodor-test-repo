package uk.co.whitbread.hotel.account.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.whitbread.hotel.account.exceptions.UnauthorizedULException;

@Aspect
@Component
@RequiredArgsConstructor
public class ApiKeyValidationAspect {

  @Value("${universal-login.api-key:}")
  private String ulApiKey;

  private static final String API_KEY_HEADER = "X-UL-API-KEY";

  @Around("@annotation(ApiKeyProtected) || @within(ApiKeyProtected)")
  public Object validateApiKey(ProceedingJoinPoint joinPoint) throws Throwable {
    if (ulApiKey == null || ulApiKey.isEmpty()) {
      return joinPoint.proceed();
    }
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) {
      throw new UnauthorizedULException("No request context available");
    }

    HttpServletRequest request = attrs.getRequest();
    String apiKey = request.getHeader(API_KEY_HEADER);

    if (!ulApiKey.equals(apiKey)) {
      throw new UnauthorizedULException("Invalid or missing API key");
    }

    return joinPoint.proceed();
  }
}

