package uk.co.whitbread.company.service.cdh;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@ExtendWith(MockitoExtension.class)
class CdhAuthorizationServiceTest {

  private static final String COMPANY_ID = "companyId";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";


  @InjectMocks
  private CdhAuthorizationService sut;

  @Test
  void isSuperAccessLevelUser_shouldReturnTrue() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel("SUPER")
        .userEmail(USER_EMAIL_FROM_TOKEN).build();
    boolean customerHasSuperAccessLevel = sut.isSuperAccessLevelUser(cdhEmployeeDetails);

    assertTrue(customerHasSuperAccessLevel);
  }

  @Test
  void isSuperAccessLevelUser_shouldReturnFalse() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel("SELF")
        .userEmail(USER_EMAIL_FROM_TOKEN).build();
    boolean customerHasSuperAccessLevel = sut.isSuperAccessLevelUser(cdhEmployeeDetails);

    assertFalse(customerHasSuperAccessLevel);
  }

  @Test
  void isSameCompany_shouldReturnTrue() {
    boolean isSameCompany = sut.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN,
        COMPANY_ACCOUNT_ID_FROM_TOKEN);

    assertTrue(isSameCompany);
  }

  @Test
  void isSameCompany_shouldReturnFalse() {
    boolean isSameCompany = sut.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID);

    assertFalse(isSameCompany);
  }

  @Test
  void isBusinessPayManagerLevelUser_shouldReturnTrue() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel("BUSINESS_PAY_MANAGER")
        .userEmail(USER_EMAIL_FROM_TOKEN).build();
    boolean result = sut.isBusinessPayManagerLevelUser(cdhEmployeeDetails);

    assertTrue(result);
  }

  @Test
  void isBusinessPayManagerLevelUser_shouldReturnFalse() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel("SELF")
        .userEmail(USER_EMAIL_FROM_TOKEN).build();
    boolean result = sut.isBusinessPayManagerLevelUser(cdhEmployeeDetails);

    assertFalse(result);
  }

}
