package uk.co.whitbread.ocd.infrastructure.rest.controller.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DateFormatValidatorTest {

  @Mock
  private DateFormat mockDateFormatAnnotation;

  private DateFormatValidator dateFormatValidator;

  @BeforeEach
  public void setUp() throws Exception {
    when(mockDateFormatAnnotation.value()).thenReturn("yyyy-MM-dd");
    dateFormatValidator = new DateFormatValidator();
    dateFormatValidator.initialize(mockDateFormatAnnotation);
  }

  @ParameterizedTest
  @CsvSource({"2016-10-22,true", "2016-aa-22,false",",true"})
  void shouldTestDateInCorrectFormat(String arg, Boolean arg2) throws Exception {
    boolean valid = dateFormatValidator.isValid(arg, null);
    assertThat(valid, is(arg2));
  }
}
