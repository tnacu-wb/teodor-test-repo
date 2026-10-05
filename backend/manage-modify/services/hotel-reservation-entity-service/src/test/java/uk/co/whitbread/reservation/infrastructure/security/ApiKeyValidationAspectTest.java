package uk.co.whitbread.reservation.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.whitbread.reservation.domain.exceptions.UnauthorizedULException;

class ApiKeyValidationAspectTest {

  private static final String SUCCESS = "success";
  private static final String DUMMY_KEY = "dummy-key";
  private static final String API_KEY_HEADER = "X-UL-API-KEY";

  private ProceedingJoinPoint joinPoint;
  private ServletRequestAttributes attributes;
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    joinPoint = mock(ProceedingJoinPoint.class);
    attributes = mock(ServletRequestAttributes.class);
    request = mock(HttpServletRequest.class);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }


  @Test
  void testProceedWhenApiKeyConfigIsEmpty() throws Throwable {
    ApiKeyValidationAspect aspect = new ApiKeyValidationAspect("");

    when(joinPoint.proceed()).thenReturn(SUCCESS);

    Object result = aspect.validateApiKey(joinPoint);
    assertEquals(SUCCESS, result);
  }

  @Test
  void testProceedWhenApiKeyConfigIsNull() throws Throwable {
    ApiKeyValidationAspect aspect = new ApiKeyValidationAspect(null);

    when(joinPoint.proceed()).thenReturn(SUCCESS);

    Object result = aspect.validateApiKey(joinPoint);
    assertEquals(SUCCESS, result);
  }

  @Test
  void testThrowWhenNoRequestContext() {
    ApiKeyValidationAspect aspect = new ApiKeyValidationAspect(DUMMY_KEY);
    RequestContextHolder.resetRequestAttributes();

    UnauthorizedULException ex = assertThrows(UnauthorizedULException.class, () -> aspect.validateApiKey(joinPoint));
    assertTrue(ex.getMessage().contains("No request context"));
  }

  @Test
  void testThrowWhenHeaderMissing() {
    ApiKeyValidationAspect aspect = new ApiKeyValidationAspect(DUMMY_KEY);
    RequestContextHolder.setRequestAttributes(attributes);

    when(attributes.getRequest()).thenReturn(request);
    when(request.getHeader(API_KEY_HEADER)).thenReturn(null);

    UnauthorizedULException ex = assertThrows(UnauthorizedULException.class, () -> aspect.validateApiKey(joinPoint));
    assertTrue(ex.getMessage().contains("Invalid or missing API key"));
  }

  @Test
  void testThrowWhenHeaderIncorrect() {
    ApiKeyValidationAspect aspect = new ApiKeyValidationAspect(DUMMY_KEY);
    RequestContextHolder.setRequestAttributes(attributes);

    when(attributes.getRequest()).thenReturn(request);
    when(request.getHeader(API_KEY_HEADER)).thenReturn("wrong");

    UnauthorizedULException ex = assertThrows(UnauthorizedULException.class, () -> aspect.validateApiKey(joinPoint));
    assertTrue(ex.getMessage().contains("Invalid or missing API key"));
  }

  @Test
  void testProceedWhenHeaderCorrect() throws Throwable {
    ApiKeyValidationAspect aspect = new ApiKeyValidationAspect(DUMMY_KEY);
    RequestContextHolder.setRequestAttributes(attributes);

    when(attributes.getRequest()).thenReturn(request);
    when(request.getHeader(API_KEY_HEADER)).thenReturn(DUMMY_KEY);
    when(joinPoint.proceed()).thenReturn(SUCCESS);
    Object result = aspect.validateApiKey(joinPoint);

    assertEquals(SUCCESS, result);
  }
}

