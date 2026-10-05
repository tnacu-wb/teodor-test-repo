package uk.co.whitbread.promo.infrastructure.rest.controller.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateFormatValidatorTest {

  @Mock
  private DateFormat mockDateFormatAnnotation;

  private DateFormatValidator sut;

  @BeforeEach
  void setUp() {
    when(mockDateFormatAnnotation.value()).thenReturn("yyyyMMdd");
    sut = new DateFormatValidator();
    sut.initialize(mockDateFormatAnnotation);
  }

  @ParameterizedTest
  @MethodSource("provideParameters")
  void shouldAcceptDateInDifferentFormats(String date, boolean expected) {
    //Act
    final boolean valid = sut.isValid(date, null);

    //Assert
    assertThat(valid, is(expected));
  }

  private static Stream<Arguments> provideParameters() {
    return Stream.of(
        Arguments.of(null, true),
        Arguments.of("2016aa22", false),
        Arguments.of("20161022", true)
    );
  }
}