package uk.co.whitbread.company.employee.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.employee.cache.CustomerCacheProvider;
import uk.co.whitbread.company.employee.client.model.AccessLevel;
import uk.co.whitbread.company.employee.client.model.Business;
import uk.co.whitbread.company.employee.client.model.Customer;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@ExtendWith(MockitoExtension.class)
public class CdhAuthorizationServiceTest {

  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeId";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";

  @Mock
  private CustomerCacheProvider cacheProvider;

  @InjectMocks
  private CdhAuthorizationService sut;

  @Test
  public void isSuperAccessLevelUser_shouldReturnTrue() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN).build();
    Customer mockCustomer = buildCustomer(buildCustomerBusinessWithSuperAccessLevel());
    when(cacheProvider.getCdhEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN,
        EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN)).thenReturn(mockCustomer);

    boolean customerHasSuperAccessLevel = sut.isSuperAccessLevelUser(cdhEmployeeDetails);

    assertTrue(customerHasSuperAccessLevel);
  }

  @Test
  public void isSuperAccessLevelUser_shouldReturnFalse() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN).build();
    Customer mockCustomer = buildCustomer(buildCustomerBusinessWithSelfAccessLevel());
    when(cacheProvider.getCdhEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN,
        EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN)).thenReturn(mockCustomer);

    boolean customerHasSuperAccessLevel = sut.isSuperAccessLevelUser(cdhEmployeeDetails);

    assertFalse(customerHasSuperAccessLevel);
  }

  @Test
  void isBusinessPayManagerLevelUser_shouldReturnTrue() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN).build();
    Customer mockCustomer = buildCustomer(buildCustomerBusinessWithBusinessPayManagerLevel());
    when(cacheProvider.getCdhEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN,
        EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN)).thenReturn(mockCustomer);

    boolean customerHasBusinessPayManagerAccessLevel = sut.isBusinessPayManagerLevelUser(cdhEmployeeDetails);

    assertTrue(customerHasBusinessPayManagerAccessLevel);
  }

  @Test
  void isBusinessPayManagerLevelUser_shouldReturnFalse() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN).build();
    Customer mockCustomer = buildCustomer(buildCustomerBusinessWithSuperAccessLevel());
    when(cacheProvider.getCdhEmployee(COMPANY_ACCOUNT_ID_FROM_TOKEN,
        EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, USER_EMAIL_FROM_TOKEN)).thenReturn(mockCustomer);

    boolean customerHasBusinessPayManagerAccessLevel = sut.isBusinessPayManagerLevelUser(cdhEmployeeDetails);

    assertFalse(customerHasBusinessPayManagerAccessLevel);
  }

  @Test
  public void isSameEmployee_shouldReturnTrue() {
    boolean isSameEmployee = sut.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN,
        EMPLOYEE_ACCOUNT_ID_FROM_TOKEN);

    assertTrue(isSameEmployee);
  }

  @Test
  public void isSameEmployee_shouldReturnFalse() {
    boolean isSameEmployee = sut.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID);

    assertFalse(isSameEmployee);
  }

  @Test
  public void isSameCompany_shouldReturnTrue() {
    boolean isSameCompany = sut.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN,
        COMPANY_ACCOUNT_ID_FROM_TOKEN);

    assertTrue(isSameCompany);
  }

  @Test
  public void isSameCompany_shouldReturnFalse() {
    boolean isSameCompany = sut.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID);

    assertFalse(isSameCompany);
  }

  private Customer buildCustomer(Business customerBusiness) {
    return Customer.builder()
        .companyId(COMPANY_ID)
        .business(customerBusiness)
        .build();
  }

  private Business buildCustomerBusinessWithSuperAccessLevel() {
    return Business.builder()
        .accessLevel(AccessLevel.SUPER)
        .employeeId(EMPLOYEE_ID)
        .build();
  }

  private Business buildCustomerBusinessWithBusinessPayManagerLevel() {
    return Business.builder()
        .accessLevel(AccessLevel.BUSINESS_PAY_MANAGER)
        .employeeId(EMPLOYEE_ID)
        .build();
  }

  private Business buildCustomerBusinessWithSelfAccessLevel() {
    return Business.builder()
        .accessLevel(AccessLevel.SELF)
        .employeeId(EMPLOYEE_ID)
        .build();
  }
}
