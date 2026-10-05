package uk.co.whitbread.company.cache;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.exceptions.EmployeeNotFoundException.ERROR_CODE;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.client.model.Customer;
import uk.co.whitbread.company.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.mapper.EmployeeMapper;
import uk.co.whitbread.company.model.feature.FeatureFlag;
import uk.co.whitbread.company.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@ExtendWith(MockitoExtension.class)
public class CustomerCacheProviderTest {

  private static final String COMPANY_ACCOUNT_ID = "98a8456e-58bb-43d5-b732-8759b3a8d435";
  private static final String EMPLOYEE_ACCOUNT_ID = "e727c6b6-6246-4056-8207-9b86c1a69f36";
  private static final String ACCESSED_BY = "user@mail.com";

  @Mock
  private EmployeeDataService employeeDataService;
  @Mock
  private EmployeeMapper employeeMapper;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private FeatureFlag featureFlag;

  @InjectMocks
  private CustomerCacheProvider cacheProvider;

  @BeforeEach
  void setUp() {
    lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
  }

  @Test
  public void getCdhEmployee_success() {
    var getEmployeeResponse = GetEmployeeResponse.builder().companyAccountId(COMPANY_ACCOUNT_ID)
        .build();
    var customer = Customer.builder().companyId(COMPANY_ACCOUNT_ID).build();
    when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY))
        .thenReturn(Optional.of(getEmployeeResponse));
    when(employeeMapper.toCustomer(getEmployeeResponse)).thenReturn(customer);

    var response = cacheProvider.getCdhEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
        ACCESSED_BY);

    assertEquals(customer, response);
  }

  @Test
  public void getCdhEmployee_withDeprecationFlag_success() {
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);

    var getEmployeeResponse = GetEmployeeResponse.builder().companyAccountId(COMPANY_ACCOUNT_ID)
        .build();
    var customer = Customer.builder().companyId(COMPANY_ACCOUNT_ID).build();
    when(employeeDataService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY))
        .thenReturn(Optional.of(getEmployeeResponse));
    when(employeeMapper.toCustomer(getEmployeeResponse)).thenReturn(customer);

    var response = cacheProvider.getCdhEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
        ACCESSED_BY);

    assertEquals(customer, response);
    verify(employeeDataService).getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);
    verify(employeeDataService, times(0)).getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);
  }

  @Test
  public void getCdhEmployee_throwsEmployeeNotFoundException() {
    when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY))
        .thenReturn(Optional.empty());

    assertThatThrownBy(
        () -> cacheProvider.getCdhEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
        .hasMessageContaining(String.format("Employee %s from company %s was not found",
            EMPLOYEE_ACCOUNT_ID, COMPANY_ACCOUNT_ID));
  }
}
