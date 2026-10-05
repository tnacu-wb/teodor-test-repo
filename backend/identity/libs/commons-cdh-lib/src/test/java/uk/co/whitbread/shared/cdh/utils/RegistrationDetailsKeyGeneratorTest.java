package uk.co.whitbread.shared.cdh.utils;

import org.junit.jupiter.api.Test;
import org.springframework.cache.interceptor.SimpleKey;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationDetailsKeyGeneratorTest {

  @Test
  void generate_returnsEmptyKey_whenNoParams() throws Exception {
    RegistrationDetailsKeyGenerator keyGen = new RegistrationDetailsKeyGenerator();
    Method method = Object.class.getMethod("toString");
    Object key = keyGen.generate(new Object(), method);
    assertEquals(SimpleKey.EMPTY, key);
  }

  @Test
  void generate_returnsKey_whenGetDashboardDetailsQueryParams() throws Exception {
    RegistrationDetailsKeyGenerator keyGen = new RegistrationDetailsKeyGenerator();
    Method method = Object.class.getMethod("toString");
    GetDashboardDetailsQueryParams params = mock(GetDashboardDetailsQueryParams.class);
    when(params.getCompanyId()).thenReturn("comp123");
    when(params.getEmployeeId()).thenReturn("emp456");

    Object key = keyGen.generate(new Object(), method, params);
    assertEquals("comp123:emp456", key);
  }

  @Test
  void generate_returnsEmptyKey_whenParamIsNotGetDashboardDetailsQueryParams() throws Exception {
    RegistrationDetailsKeyGenerator keyGen = new RegistrationDetailsKeyGenerator();
    Method method = Object.class.getMethod("toString");
    Object key = keyGen.generate(new Object(), method, "someString");
    assertEquals(SimpleKey.EMPTY, key);
  }

  @Test
  void generateKey_staticMethod_returnsFormattedKey() {
    String key = RegistrationDetailsKeyGenerator.generateKey("c1", "e2");
    assertEquals("c1:e2", key);
  }
}