package uk.co.whitbread.spending.infrastructure.rest.controller.spending.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.validation.DateFormat;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.validation.DateFormatValidator;

/**
 * Created by Oleksandr Murha on 04/11/2016.
 */
@ExtendWith(MockitoExtension.class)
class DateFormatValidatorTest {

  @Mock
  private DateFormat mockDateFormatAnnotation;

  private DateFormatValidator sut;

  @BeforeEach
  void setUp() {
    when(mockDateFormatAnnotation.value()).thenReturn("MM-yyyy");
    sut = new DateFormatValidator();
    sut.initialize(mockDateFormatAnnotation);
  }

  @ParameterizedTest
  @ValueSource(strings = {"aa-2024", "11-12o1", "23-09-2024"})
  void shouldFailValidationOfDateInWrongFormat(String date) {
    // Act
    boolean valid = sut.isValid(date, null);

    // Assert
    assertFalse(valid);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"11-2024"})
  void shouldAcceptDateInCorrectFormat(String date) {
    // Act
    boolean valid = sut.isValid(date, null);

    // Assert
    assertTrue(valid);
  }
}
