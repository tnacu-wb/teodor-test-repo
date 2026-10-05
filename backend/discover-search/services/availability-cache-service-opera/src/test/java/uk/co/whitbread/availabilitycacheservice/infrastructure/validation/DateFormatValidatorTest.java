package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;


public class DateFormatValidatorTest {

  private DateFormatValidator dateFormatValidator;

  private SearchCriteria criteria;
  private LocalDate today;
  private DateTimeFormatter dtf;
  private boolean isValid;

  @BeforeEach
  public void setUp() {
    dateFormatValidator = new DateFormatValidator();
    dateFormatValidator.setFormat("yyyy-MM-dd");
    criteria = new SearchCriteria();
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

    isValid = dateFormatValidator.isValid("invalid", null);
    assertThat(isValid).isFalse();

    isValid = dateFormatValidator.isValid("2020-14-12 00:00", null);
    assertThat(isValid).isFalse();

    isValid = dateFormatValidator.isValid("12-12-2020", null);
    assertThat(isValid).isFalse();
  }
}
