package uk.co.whitbread.shared.cdh.utils;

import java.lang.reflect.Method;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;

@Component
public class RegistrationDetailsKeyGenerator implements KeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    if (params.length == 0) {
      return SimpleKey.EMPTY;
    }
    var param = params[0];
    if (param instanceof GetDashboardDetailsQueryParams) {
      GetDashboardDetailsQueryParams queryParams = (GetDashboardDetailsQueryParams) param;
      return generateKey(queryParams.getCompanyId(), queryParams.getEmployeeId());
    }
    return SimpleKey.EMPTY;
  }

  public static String generateKey(String companyId, String employeeId) {
    return String.format("%s:%s", companyId, employeeId);
  }
}
