package uk.co.whitbread.cdh.infrastructure.rest.controller.validation;

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
class DateFormatValidatorTest {

  @Mock
  private DateFormat mockDateFormatAnnotation;

  private DateFormatValidator sut;

  @BeforeEach
  void setUp() {
    when(mockDateFormatAnnotation.value()).thenReturn("yyyy-MM-dd");
    sut = new DateFormatValidator();
    sut.initialize(mockDateFormatAnnotation);
  }

  @ParameterizedTest
  @CsvSource({"2016-10-22,true", "2016-aa-22,false",",true"})
  void shouldTestDateInCorrectFormat(String arg, Boolean arg2) {
    boolean valid = sut.isValid(arg, null);
    assertThat(valid, is(arg2));
  }
}
