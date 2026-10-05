package uk.co.whitbread.hotel.card.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.card.exceptions.EmployeeNotFoundException.ERROR_CODE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.client.account.model.AccessLevel;
import uk.co.whitbread.hotel.card.client.account.model.Business;
import uk.co.whitbread.hotel.card.client.account.model.Customer;
import uk.co.whitbread.hotel.card.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.hotel.card.mapper.EmployeeMapper;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.EmployeeDataService;

@ExtendWith(MockitoExtension.class)
public class CustomerCacheProviderTest {

  private static final String COMPANY_ID = "companyAccountId";
  private static final String EMPLOYEE_ID = "employeeAccountId";
  private static final String USER_EMAIL = "a@b.c";

  @Mock
  private EmployeeDataService mockEmployeeDataService;
  @Mock
  private EmployeeMapper mockEmployeeMapper;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @InjectMocks
  private CustomerCacheProvider mockCustomerCacheProvider;

  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    objectMapper = new ObjectMapper();
    var featureFlag = new FeatureFlag();
    featureFlag.setCdhApiDeprecation(new FeatureFlag.Feature());
    lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
  }

  @Test
  public void getCdhEmployee_Success() throws IOException {
    var getEmployeeResponse = objectMapper.readValue(
        new File("src/test/resources/mapping/GetEmployeeResponse.json"),
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);

    Customer customer = Customer.builder().companyId(COMPANY_ID).business(new Business(
        AccessLevel.SUPER, EMPLOYEE_ID)).build();

    when(mockEmployeeDataService.getEmployeeV2(COMPANY_ID,
        EMPLOYEE_ID, USER_EMAIL)).thenReturn(
        Optional.of(getEmployeeResponse));
    when(mockEmployeeMapper.toCustomer(getEmployeeResponse)).thenReturn(customer);

    mockCustomerCacheProvider.getCdhEmployee(COMPANY_ID,
        EMPLOYEE_ID, USER_EMAIL);

    assertThat(customer).isNotNull();
  }

  @Test
  public void getCdhEmployee_ThrowsEmployeeNotFoundException() {
    when(mockEmployeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL)).thenReturn(
        Optional.empty());

    // Then
    assertThatThrownBy(
        () -> mockCustomerCacheProvider.getCdhEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL))
        .isInstanceOf(EmployeeNotFoundException.class)
        .hasFieldOrPropertyWithValue("errorCode", ERROR_CODE)
        .hasMessageContaining(
            String.format("Employee %s from company %s was not found", EMPLOYEE_ID, COMPANY_ID));
  }

  @Test
  public void getCdhEmployee_flagDisabled_usesLegacyGetEmployeeCall() throws IOException {
    var getEmployeeResponse = objectMapper.readValue(
        new File("src/test/resources/mapping/GetEmployeeResponse.json"),
        uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class);

    Customer customer = Customer.builder().companyId(COMPANY_ID).business(new Business(
        AccessLevel.SUPER, EMPLOYEE_ID)).build();

    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();
    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(mockEmployeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL))
        .thenReturn(Optional.of(getEmployeeResponse));
    when(mockEmployeeMapper.toCustomer(getEmployeeResponse)).thenReturn(customer);

    mockCustomerCacheProvider.getCdhEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL);

    verify(mockEmployeeDataService).getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL);
    verify(mockEmployeeDataService, times(0)).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL);
  }
}
