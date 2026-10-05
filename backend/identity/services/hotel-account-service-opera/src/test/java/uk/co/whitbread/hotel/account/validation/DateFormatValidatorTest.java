package uk.co.whitbread.hotel.account.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    void setUp() {
        when(mockDateFormatAnnotation.value()).thenReturn("yyyy-MM-dd");
        sut = new DateFormatValidator();
        sut.initialize(mockDateFormatAnnotation);
    }

    @Test
    void shouldAcceptDateInCorrectFormat() {
        //Given
        String date = "2016-10-22";

        //When
        boolean valid = sut.isValid(date, null);

        //Then
        assertThat(valid, is(true));
    }

    @Test
    void shouldFailValidationOfDateInWrongFormat() {
        //Given
        String date = "2016-aa-22";

        //When
        boolean valid = sut.isValid(date, null);

        //Then
        assertThat(valid, is(false));
    }

    @Test
    void shouldIgnoreNullValue() {
        //Given
        String date = null;

        //When
        boolean valid = sut.isValid(date, null);

        //Then
        assertThat(valid, is(true));
    }
}
