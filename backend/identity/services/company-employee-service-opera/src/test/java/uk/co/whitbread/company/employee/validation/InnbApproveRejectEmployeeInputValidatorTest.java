package uk.co.whitbread.company.employee.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.whitbread.company.employee.exceptions.InvalidOperationException;
import uk.co.whitbread.company.employee.exceptions.InvalidTokenException;
import uk.co.whitbread.company.employee.exceptions.RestrictedOperationException;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.AccessLevel;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

class InnbApproveRejectEmployeeInputValidatorTest {

  private final InnbEmployeeValidator validator = new InnbEmployeeValidator();

  @Test
  void validateTokenClaims_WhenAllFieldsAreValid_ThenNoException() {
    var travelManagerDetails = CdhEmployeeDetails.builder()
          .companyAccountId("company123")
          .employeeAccountId("employee123")
          .userEmail("user@example.com")
          .build();

    assertDoesNotThrow(() -> validator.validateTokenClaims(travelManagerDetails));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidCdhEmployeeDetails")
  void validateTokenClaims_WhenFieldsAreInvalid_ThenThrowInvalidTokenException(CdhEmployeeDetails travelManagerDetails) {
    assertThrows(InvalidTokenException.class, () -> validator.validateTokenClaims(travelManagerDetails));
  }

  @Test
  void validateSuperAccessLevel_WhenAccessLevelIsSuper_ThenNoException() {
    var cdhTravelManager = GetEmployeeResponse.builder()
          .accessLevel("sUpEr")
          .employeeAccountId("employee123")
          .companyAccountId("company123")
          .build();

    assertDoesNotThrow(() -> validator.validateManagerAccessLevel(cdhTravelManager));
  }

  @Test
  void validateSuperAccessLevel_WhenAccessLevelIsBusinessPayManager_ThenNoException() {
    var cdhTravelManager = GetEmployeeResponse.builder()
        .accessLevel("BUSINESS_PAY_MANAGER")
        .employeeAccountId("employee123")
        .companyAccountId("company123")
        .build();

    assertDoesNotThrow(() -> validator.validateManagerAccessLevel(cdhTravelManager));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidAccessLevels")
  void validateSuperAccessLevel_WhenAccessLevelIsNotSuper_ThenThrowRestrictedOperationException(String accessLevel) {
    var cdhTravelManager = GetEmployeeResponse.builder()
          .accessLevel(accessLevel)
          .employeeAccountId("employee123")
          .companyAccountId("company123")
          .build();

    var exception = assertThrows(RestrictedOperationException.class, () -> validator.validateManagerAccessLevel(cdhTravelManager));
    assertEquals("The provided token does not belong to a Manager.", exception.getMessage());
  }

  @Test
  void validateEmployeeSameCompanyWithTravelManager_WhenCompaniesAreSame_ThenNoException() {
    var cdhTravelManager = GetEmployeeResponse.builder()
          .companyAccountId("company123")
          .employeeAccountId("travelManager123")
          .build();
    var cdhEmployee = GetEmployeeResponse.builder()
          .companyAccountId("company123")
          .employeeAccountId("employee123")
          .build();

    assertDoesNotThrow(() -> validator.validateEmployeeSameCompanyWithManager(cdhTravelManager, cdhEmployee));
  }

  @ParameterizedTest
  @MethodSource("provideDifferentCompanyIds")
  void validateEmployeeSameCompanyWithTravelManager_WhenCompaniesAreDifferent_ThenThrowInvalidOperationException(
        String travelManagerCompanyId, String employeeCompanyId) {
    var cdhTravelManager = GetEmployeeResponse.builder()
          .companyAccountId(travelManagerCompanyId)
          .employeeAccountId("travelManager123")
          .build();
    var cdhEmployee = GetEmployeeResponse.builder()
          .companyAccountId(employeeCompanyId)
          .employeeAccountId("employee123")
          .build();

    var exception = assertThrows(InvalidOperationException.class,
          () -> validator.validateEmployeeSameCompanyWithManager(cdhTravelManager, cdhEmployee));
    assertEquals("The employee does not belong to the same company as the Manager.", exception.getMessage());
  }

  @Test
  void validateUserAndTravelManagerStatus_WhenValidState_ThenNoException() {
    var cdhTravelManager = GetEmployeeResponse.builder()
          .employeeAccountId("travelManager123")
          .employeeStatus("ACTIVE")
          .build();
    var cdhEmployee = GetEmployeeResponse.builder()
          .employeeAccountId("employee123")
          .employeeStatus("INACTIVE")
          .awaitingApproval(true)
          .activationKey("validKey")
          .build();

    assertDoesNotThrow(() -> validator.validateUserAndManagerStatus(cdhTravelManager, cdhEmployee));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidStates")
  void validateUserAndTravelManagerStatus_WhenInvalidState_ThenThrowInvalidOperationException(
        GetEmployeeResponse cdhTravelManager, GetEmployeeResponse cdhEmployee) {
    // Act & Assert
    var exception = assertThrows(InvalidOperationException.class,
          () -> validator.validateUserAndManagerStatus(cdhTravelManager, cdhEmployee));
    assertEquals("The employee or the Manager is not in the correct state.", exception.getMessage());
  }

  @ParameterizedTest
  @MethodSource("provideValidManagerAndEmployeeAccessLevels")
  void validateEmployeeAccessLevelsBasedOnManagerAccessLevel_WhenValid_ThenNoException(
      String managerAccessLevel,
      uk.co.whitbread.company.employee.model.AccessLevel employeeAccessLevel) {
    assertDoesNotThrow(
        () -> validator.validateEmployeeAccessLevelsBasedOnManagerAccessLevel(managerAccessLevel,
            employeeAccessLevel));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidManagerAndEmployeeAccessLevels")
  void validateEmployeeAccessLevelsBasedOnManagerAccessLevel_WhenInvalid_ThenThrowInvalidOperationException(
      String managerAccessLevel,
      uk.co.whitbread.company.employee.model.AccessLevel employeeAccessLevel) {
    assertThrows(InvalidOperationException.class,
        () -> validator.validateEmployeeAccessLevelsBasedOnManagerAccessLevel(managerAccessLevel,
            employeeAccessLevel));
  }

  private static Stream<Arguments> provideValidManagerAndEmployeeAccessLevels() {
    return Stream.of(
        Arguments.of("SUPER", uk.co.whitbread.company.employee.model.AccessLevel.STAYER),
        Arguments.of("SUPER", uk.co.whitbread.company.employee.model.AccessLevel.SELF),
        Arguments.of("SUPER", uk.co.whitbread.company.employee.model.AccessLevel.BOOKER),
        Arguments.of("SUPER", uk.co.whitbread.company.employee.model.AccessLevel.SUPER),
        Arguments.of("BUSINESS_PAY_MANAGER",
            uk.co.whitbread.company.employee.model.AccessLevel.BUSINESS_PAY_MANAGER),
        Arguments.of("BUSINESS_PAY_MANAGER",
            uk.co.whitbread.company.employee.model.AccessLevel.BUSINESS_PAY_USER)
    );
  }

  private static Stream<Arguments> provideInvalidManagerAndEmployeeAccessLevels() {
    return Stream.of(
        Arguments.of("SUPER",
            uk.co.whitbread.company.employee.model.AccessLevel.BUSINESS_PAY_MANAGER),
        Arguments.of("SUPER", uk.co.whitbread.company.employee.model.AccessLevel.BUSINESS_PAY_USER),
        Arguments.of("BUSINESS_PAY_MANAGER",
            uk.co.whitbread.company.employee.model.AccessLevel.STAYER),
        Arguments.of("BUSINESS_PAY_MANAGER",
            uk.co.whitbread.company.employee.model.AccessLevel.SELF),
        Arguments.of("BUSINESS_PAY_MANAGER",
            uk.co.whitbread.company.employee.model.AccessLevel.BOOKER),
        Arguments.of("BUSINESS_PAY_MANAGER",
            uk.co.whitbread.company.employee.model.AccessLevel.SUPER),
        Arguments.of("UNKNOWN", uk.co.whitbread.company.employee.model.AccessLevel.STAYER),
        Arguments.of("", uk.co.whitbread.company.employee.model.AccessLevel.SELF)
    );
  }

  private static Stream<CdhEmployeeDetails> provideInvalidCdhEmployeeDetails() {
    return Stream.of(
          CdhEmployeeDetails.builder().companyAccountId("").employeeAccountId("employee123").employeeAccountId("user@example.com").build(),
          CdhEmployeeDetails.builder().companyAccountId("company123").employeeAccountId("").employeeAccountId("user@example.com").build(),
          CdhEmployeeDetails.builder().companyAccountId("company123").employeeAccountId("employee123").employeeAccountId("").build(),
          CdhEmployeeDetails.builder().companyAccountId(null).employeeAccountId("employee123").employeeAccountId("user@example.com").build(),
          CdhEmployeeDetails.builder().companyAccountId("company123").employeeAccountId(null).employeeAccountId("user@example.com").build(),
          CdhEmployeeDetails.builder().companyAccountId("company123").employeeAccountId("employee123").employeeAccountId(null).build(),
          CdhEmployeeDetails.builder().companyAccountId(null).employeeAccountId(null).employeeAccountId(null).build()
    );
  }

  private static Stream<String> provideInvalidAccessLevels() {
    return Arrays.stream(AccessLevel.values())
          .map(AccessLevel::toString)
          .filter(string -> !string.equalsIgnoreCase("super"))
          .filter(string -> !string.equalsIgnoreCase("BUSINESS_PAY_MANAGER"));
  }

  private static Stream<String[]> provideDifferentCompanyIds() {
    return Stream.of(
          new String[]{"company123", "company456"},
          new String[]{"company123", ""},
          new String[]{"", "company123"},
          new String[]{"company123", null},
          new String[]{null, "company123"},
          new String[]{null, null}
    );
  }

  private static Stream<Object[]> provideInvalidStates() {
    return Stream.of(
          // Case 1: Employee status is not INACTIVE
          new Object[]{
                GetEmployeeResponse.builder()
                      .employeeAccountId("travelManager123")
                      .employeeStatus("ACTIVE")
                      .build(),
                GetEmployeeResponse.builder()
                      .employeeAccountId("employee123")
                      .employeeStatus("ACTIVE")
                      .awaitingApproval(true)
                      .activationKey("validKey")
                      .build()
          },
          // Case 2: Employee is not awaiting approval
          new Object[]{
                GetEmployeeResponse.builder()
                      .employeeAccountId("travelManager123")
                      .employeeStatus("ACTIVE")
                      .build(),
                GetEmployeeResponse.builder()
                      .employeeAccountId("employee123")
                      .employeeStatus("INACTIVE")
                      .awaitingApproval(false)
                      .activationKey("validKey")
                      .build()
          },
          // Case 3: Employee activation key is blank
          new Object[]{
                GetEmployeeResponse.builder()
                      .employeeAccountId("travelManager123")
                      .employeeStatus("ACTIVE")
                      .build(),
                GetEmployeeResponse.builder()
                      .employeeAccountId("employee123")
                      .employeeStatus("INACTIVE")
                      .awaitingApproval(true)
                      .activationKey("")
                      .build()
          },
          // Case 4: Travel Manager status is INACTIVE
          new Object[]{
                GetEmployeeResponse.builder()
                      .employeeAccountId("travelManager123")
                      .employeeStatus("INACTIVE")
                      .build(),
                GetEmployeeResponse.builder()
                      .employeeAccountId("employee123")
                      .employeeStatus("INACTIVE")
                      .awaitingApproval(true)
                      .activationKey("validKey")
                      .build()
          }
    );
  }
}
