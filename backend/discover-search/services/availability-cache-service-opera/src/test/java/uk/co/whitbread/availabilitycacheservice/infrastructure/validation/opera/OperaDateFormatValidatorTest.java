package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaSearchCriteria;


public class OperaDateFormatValidatorTest {

  private OperaDateFormatValidator dateFormatValidator;

  private OperaSearchCriteria criteria;
  private LocalDate today;
  private DateTimeFormatter dtf;
  private boolean isValid;

  @BeforeEach
  public void setUp() {
    dateFormatValidator = new OperaDateFormatValidator();
    dateFormatValidator.setFormat("yyyy-MM-dd");
    criteria = new OperaSearchCriteria();
    dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    today = LocalDate.now();
  }

  @Test
  public void successTestForDateFormat() {
    isValid = dateFormatValidator.isValid(today.format(dtf), null);
    assertThat(isValid).isTrue();
  }

  @Test
  public void failureTestForNullDate() {
    isValid = dateFormatValidator.isValid(null, null);
    assertThat(isValid).isFalse();

    //failureTestForInvalidDate
    isValid = dateFormatValidator.isValid("invalid", null);
    assertThat(isValid).isFalse();
  }

  @Test
  public void failureTestForInvalidDateFormat() {
    isValid = dateFormatValidator.isValid("2020-14-12 00:00", null);
    assertThat(isValid).isFalse();

    //failureTestForInvalidDateFormatter
    isValid = dateFormatValidator.isValid("12-12-2020", null);
    assertThat(isValid).isFalse();
  }

}
