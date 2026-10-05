package uk.co.whitbread.company.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.ACCESSED_BY;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.ACCESS_CONTEXT;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.COMPANY_ID;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.EN;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.FROM_DATE;
import static uk.co.whitbread.company.utils.CompanyReportUtilsTest.TO_DATE;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.company.domain.model.validation.ValidatorFactory;

class ManagementInformationRequestTest {
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> new ManagementInformationRequest(FROM_DATE, TO_DATE,
        true, COMPANY_ID, ACCESS_CONTEXT, ACCESSED_BY, EN));
  }

}
