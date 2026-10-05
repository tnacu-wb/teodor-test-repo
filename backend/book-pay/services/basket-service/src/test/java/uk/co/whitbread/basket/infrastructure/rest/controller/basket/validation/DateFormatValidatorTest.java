package uk.co.whitbread.basket.infrastructure.rest.controller.basket.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DateFormatValidatorTest {

    @Mock
    private DateFormat mockDateFormatAnnotation;

    private DateFormatValidator sut;

    @BeforeEach
    public void setUp() throws Exception {
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

    @ParameterizedTest
    @ValueSource(strings = {"2016-aa-22", ""})
    void shouldFailValidationOfDateInWrongFormat(String date) throws Exception {

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