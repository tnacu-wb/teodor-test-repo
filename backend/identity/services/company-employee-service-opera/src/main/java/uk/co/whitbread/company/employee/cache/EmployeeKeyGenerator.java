package uk.co.whitbread.company.employee.cache;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Component;

@Component
public class EmployeeKeyGenerator extends SimpleKeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    if (params.length > 0) {
      return super.generate(target, method, Arrays.copyOfRange(params, 0, params.length - 1));
    }
    return super.generate(target, method, params);
  }
}
