package uk.co.whitbread.hotel.card.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
@ExtendWith(MockitoExtension.class)
class CdhInnBusinessCardsServiceTest {

  public static final String COMPANY_ACCOUNT_ID = "companyAccountId";
  public static final String EMPLOYEE_ACCOUNT_ID = "employeeAccountId";
  public static final String ACCESSED_BY = "userEmail";
  public static final String BART_EMPLOYEE_ID = "bartEmployeeId";
  public static final String EMAIL_ADDRESS = "emailAddress";

  @Mock
  private EmployeeDataService employeeDataService;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @InjectMocks
  private CdhInnBusinessCardsService cdhInnBusinessCardsService;

  @BeforeEach
  void setUp() {
    var featureFlag = new FeatureFlag();
    featureFlag.setCdhApiDeprecation(new FeatureFlag.Feature());
    lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
  }

  @Test
  void getEmployeeShouldReturn200Ok() {

    // Arrange
    when(employeeDataService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY))
        .thenReturn(createGetEmployeeResponse());

    // Act
    var response = cdhInnBusinessCardsService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);

    // Assert
    assertNotNull(response);
    assertEquals(BART_EMPLOYEE_ID, response.getBartEmployeeId());
    assertEquals(EMAIL_ADDRESS, response.getEmailAddress());
  }

  @Test
  void getEmployeeShouldThrowException() {

    // Arrange
    when(employeeDataService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY))
        .thenReturn(Optional.empty());

    // Act & Assert
    Assertions.assertThrows(EmployeeNotFoundException.class, () ->
        cdhInnBusinessCardsService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY));


  }

  @Test
  void getEmployee_flagDisabled_usesLegacyGetEmployeeCall() {
    // Given
    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();
    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY))
        .thenReturn(createGetEmployeeResponse());

    // Act
    var response = cdhInnBusinessCardsService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);

    // Assert
    assertNotNull(response);
    verify(employeeDataService).getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);
    verify(employeeDataService, times(0)).getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);
  }

  private Optional<GetEmployeeResponse> createGetEmployeeResponse() {
    return Optional.ofNullable(GetEmployeeResponse.builder()
            .bartEmployeeId(BART_EMPLOYEE_ID)
            .emailAddress(EMAIL_ADDRESS)
        .build()
    );
  }
}
