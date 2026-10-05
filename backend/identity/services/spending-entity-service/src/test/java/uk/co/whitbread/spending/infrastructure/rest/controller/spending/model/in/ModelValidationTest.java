package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.domain.model.in.Scheme;
import uk.co.whitbread.spending.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.validation.ModelValidator;

@ExtendWith(MockitoExtension.class)
class ModelValidationTest {

  private static final String PIBA_ACCOUNT_ID = "PIBA_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String FROM_MONTH_YEAR = "11-2024";
  private static final String TO_MONTH_YEAR = "09-2025";
  private static final String LANGUAGE = "EN";

  @SuppressWarnings("unused")
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @ParameterizedTest
  @MethodSource({"createCompanySpendingRequestDto", "createAccountSpendingRequestDto"})
  void constructor__shouldSelfValidateOk(ModelValidator<?> request) {

    assertDoesNotThrow(request::validate);
  }

  static Stream<CompanySpendingRequestDto> createCompanySpendingRequestDto() {
    return Stream.of(new CompanySpendingRequestDto(FROM_MONTH_YEAR, TO_MONTH_YEAR));
  }

  static Stream<AccountSpendingRequestDto> createAccountSpendingRequestDto() {
    return Stream.of(new AccountSpendingRequestDto(PIBA_ACCOUNT_ID, FROM_MONTH_YEAR, TO_MONTH_YEAR, LANGUAGE, Scheme.GB,null));
  }

}
