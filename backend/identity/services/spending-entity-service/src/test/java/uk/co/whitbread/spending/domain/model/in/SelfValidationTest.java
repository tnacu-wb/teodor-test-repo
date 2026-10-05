package uk.co.whitbread.spending.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest.AccountSpendingRequestBuilder;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest.CompanySpendingRequestBuilder;
import uk.co.whitbread.spending.domain.model.validation.DomainValidator;
import uk.co.whitbread.spending.domain.model.validation.ValidatorFactory;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class SelfValidationTest {

  private static final String COMPANY_ACCOUNT_ID = "COMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String PIBA_ACCOUNT_ID = "PIBA_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String FROM_MONTH_YEAR = "11-2024";
  private static final String TO_MONTH_YEAR = "09-2025";

  @SuppressWarnings("unused")
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void constructor_invalidCompanyAccountId_shouldNotThrowExeption() {
    assertDoesNotThrow(() -> CompanySpendingRequest.builder()
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build());
  }
  @Test
  void constructor_invalidPibaAccountId_shouldSelfValidateAndThrow() {
    String expectedMessage = "pibaAccountId: must not be empty";

    checkErrorThrown(() -> AccountSpendingRequest.builder()
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR)
        .build(), expectedMessage);
  }

  @ParameterizedTest
  @ValueSource(strings = {"companySpendingContainsInvalidFromMonthYear",
      "accountSpendingContainsInvalidFromMonthYear"})
  void constructor_invalidFromMonthYear_shouldSelfValidateAndThrow(String type) {
    String expectedMessage = "fromMonthYear: must not be empty";

    checkErrorThrown(() -> getObject(type), expectedMessage);
  }

  @ParameterizedTest
  @ValueSource(strings = {"companySpendingContainsInvalidToMonthYear",
      "accountSpendingContainsInvalidToMonthYear"})
  void constructor_invalidToMonthYear_shouldSelfValidateAndThrow(String type) {
    String expectedMessage = "toMonthYear: must not be empty";

    checkErrorThrown(() -> getObject(type), expectedMessage);
  }

  @ParameterizedTest
  @ValueSource(strings = {"companySpending", "accountSpending"})
  void constructor__shouldSelfValidateOk(String type) {

    assertDoesNotThrow(() -> {
      getObject(type);
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {

    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  private static <T extends DomainValidator> T getObject(String nameObject) {

    CompanySpendingRequestBuilder companySpending = CompanySpendingRequest.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR);

    AccountSpendingRequestBuilder accountSpendingRequest = AccountSpendingRequest.builder()
        .pibaAccountId(PIBA_ACCOUNT_ID)
        .fromMonthYear(FROM_MONTH_YEAR)
        .toMonthYear(TO_MONTH_YEAR);

    switch (nameObject) {
      case "companySpending":
        return (T) companySpending.build();
      case "accountSpending":
        return (T) accountSpendingRequest.build();
      case "companySpendingContainsInvalidToMonthYear":
        return (T) companySpending.toMonthYear("").build();
      case "accountSpendingContainsInvalidToMonthYear":
        return (T) accountSpendingRequest.toMonthYear("").build();
      case "companySpendingContainsInvalidFromMonthYear":
        return (T) companySpending.fromMonthYear("").build();
      case "accountSpendingContainsInvalidFromMonthYear":
        return (T) accountSpendingRequest.fromMonthYear("").build();
      default:
        return null;
    }
  }
}
