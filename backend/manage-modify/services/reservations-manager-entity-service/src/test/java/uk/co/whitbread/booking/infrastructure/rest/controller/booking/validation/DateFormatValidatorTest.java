package uk.co.whitbread.booking.infrastructure.rest.controller.booking.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Created by Oleksandr Murha on 04/11/2016.
 */
@ExtendWith(MockitoExtension.class)
class DateFormatValidatorTest {

  @Mock
  private DateFormat mockDateFormatAnnotation;

  private DateFormatValidator sut;

  @BeforeEach
  public void setUp() {
    when(mockDateFormatAnnotation.value()).thenReturn("yyyy-MM-dd");
    sut = new DateFormatValidator();
    sut.initialize(mockDateFormatAnnotation);
  }

  @Test
  void shouldAcceptDateInCorrectFormat() throws Exception {
    //Arrange
    String date = "2016-10-22";

    //Act
    boolean valid = sut.isValid(date, null);

    //Assert
    assertThat(valid, is(true));
  }

  @Test
  void shouldFailValidationOfDateInWrongFormat() throws Exception {
    //Arrange
    String date = "2016-aa-22";

    //Act
    boolean valid = sut.isValid(date, null);

    //Assert
    assertThat(valid, is(false));
  }

  @Test
  void shouldIgnoreNullValue() throws Exception {
    //Arrange
    String date = null;

    //Act
    boolean valid = sut.isValid(date, null);

    //Assert
    assertThat(valid, is(true));
  }
}