package uk.co.whitbread.hotel.account.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.whitbread.hotel.account.exceptions.UnauthorizedULException;

class ApiKeyValidationAspectTest {

  private ApiKeyValidationAspect aspect;
  private ProceedingJoinPoint joinPoint;
  private ServletRequestAttributes attributes;
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    aspect = new ApiKeyValidationAspect();
    joinPoint = mock(ProceedingJoinPoint.class);
    attributes = mock(ServletRequestAttributes.class);
    request = mock(HttpServletRequest.class);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  private void setApiKey(String value) throws Exception {
    Field field = ApiKeyValidationAspect.class.getDeclaredField("ulApiKey");
    field.setAccessible(true);
    field.set(aspect, value);
  }

  @Test
  void testProceedWhenApiKeyConfigIsEmpty() throws Throwable {
    setApiKey("");

    when(joinPoint.proceed()).thenReturn("success");

    Object result = aspect.validateApiKey(joinPoint);
    assertEquals("success", result);
  }

  @Test
  void testProceedWhenApiKeyConfigIsNull() throws Throwable {
    setApiKey(null);

    when(joinPoint.proceed()).thenReturn("success");

    Object result = aspect.validateApiKey(joinPoint);
    assertEquals("success", result);
  }

  @Test
  void testThrowWhenNoRequestContext() throws Exception {
    setApiKey("dummy-key");
    RequestContextHolder.resetRequestAttributes();

    UnauthorizedULException ex = assertThrows(UnauthorizedULException.class, () -> aspect.validateApiKey(joinPoint));
    assertTrue(ex.getMessage().contains("No request context"));
  }

  @Test
  void testThrowWhenHeaderMissing() throws Exception {
    setApiKey("dummy-key");
    RequestContextHolder.setRequestAttributes(attributes);

    when(attributes.getRequest()).thenReturn(request);
    when(request.getHeader("X-UL-API-KEY")).thenReturn(null);

    UnauthorizedULException ex = assertThrows(UnauthorizedULException.class, () -> aspect.validateApiKey(joinPoint));
    assertTrue(ex.getMessage().contains("Invalid or missing API key"));
  }

  @Test
  void testThrowWhenHeaderIncorrect() throws Exception {
    setApiKey("dummy-key");
    RequestContextHolder.setRequestAttributes(attributes);

    when(attributes.getRequest()).thenReturn(request);
    when(request.getHeader("X-UL-API-KEY")).thenReturn("wrong");

    UnauthorizedULException ex = assertThrows(UnauthorizedULException.class, () -> aspect.validateApiKey(joinPoint));
    assertTrue(ex.getMessage().contains("Invalid or missing API key"));
  }

  @Test
  void testProceedWhenHeaderCorrect() throws Throwable {
    setApiKey("dummy-key");
    RequestContextHolder.setRequestAttributes(attributes);

    when(attributes.getRequest()).thenReturn(request);
    when(request.getHeader("X-UL-API-KEY")).thenReturn("dummy-key");
    when(joinPoint.proceed()).thenReturn("success");
    Object result = aspect.validateApiKey(joinPoint);

    assertEquals("success", result);
  }
}

