package uk.co.whitbread.hotel.card.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@ExtendWith(MockitoExtension.class)
public class CdhAuthorizationServiceTest {

  private static final String COMPANY_ID = "companyId";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";


  @InjectMocks
  private CdhAuthorizationService sut;

  @Test
  public void isSuperAccessLevelUser_shouldReturnTrue() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel("SUPER")
        .userEmail(USER_EMAIL_FROM_TOKEN).build();

    boolean customerHasSuperAccessLevel = sut.isSuperAccessLevelUser(cdhEmployeeDetails);

    assertTrue(customerHasSuperAccessLevel);
  }

  @Test
  public void isSuperAccessLevelUser_shouldReturnFalse() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .accessLevel("SELF")
        .userEmail(USER_EMAIL_FROM_TOKEN).build();

    boolean customerHasSuperAccessLevel = sut.isSuperAccessLevelUser(cdhEmployeeDetails);

    assertFalse(customerHasSuperAccessLevel);
  }

  @Test
  public void isSameCompany_shouldReturnTrue() {
    boolean isSameCompany = sut.isSameEntity(COMPANY_ACCOUNT_ID_FROM_TOKEN,
        COMPANY_ACCOUNT_ID_FROM_TOKEN);

    assertTrue(isSameCompany);
  }

  @Test
  public void isSameCompany_shouldReturnFalse() {
    boolean isSameCompany = sut.isSameEntity(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID);

    assertFalse(isSameCompany);
  }
}
