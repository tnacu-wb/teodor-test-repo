package uk.co.whitbread.common.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class DateFormatValidatorTest {

    @Mock
    private DateFormat mockDateFormatAnnotation;

    private DateFormatValidator underTest;

    @BeforeEach
    public void setUp() throws Exception {
        when(mockDateFormatAnnotation.value()).thenReturn("yyyy-MM-dd");
        when(mockDateFormatAnnotation.nullable()).thenReturn(true);
        underTest = new DateFormatValidator();
        underTest.initialize(mockDateFormatAnnotation);
    }

    @Test
    public void shouldAcceptDateInCorrectFormat() throws Exception {
        //Given
        String date = "2016-10-22";

        //When
        boolean valid = underTest.isValid(date, null);

        //Then
        assertThat(valid, is(true));
    }

    @Test
    public void shouldFailValidationOfDateInWrongFormat() throws Exception {
        //Given
        String date = "2016-aa-22";

        //When
        boolean valid = underTest.isValid(date, null);

        //Then
        assertThat(valid, is(false));
    }

    @Test
    public void shouldIgnoreNullValueWhenNullableIsTrue() throws Exception {
        //Given
        String date = null;

        //When
        boolean valid = underTest.isValid(date, null);

        //Then
        assertThat(valid, is(true));
    }

    @Test
    public void shouldFailNullValueWhenNullableIsFalse() throws Exception {
        //Given
        when(mockDateFormatAnnotation.value()).thenReturn("yyyy-MM-dd");
        when(mockDateFormatAnnotation.nullable()).thenReturn(false);
        underTest = new DateFormatValidator();
        underTest.initialize(mockDateFormatAnnotation);

        ConstraintValidatorContext mockContext = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder mockConstraintViolationBuilder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(mockContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mockConstraintViolationBuilder);

        String date = null;

        //When
        boolean valid = underTest.isValid(date, mockContext);

        //Then
        verify(mockContext).buildConstraintViolationWithTemplate("may not be empty");
        verify(mockConstraintViolationBuilder).addConstraintViolation();
        assertThat(valid, is(false));
    }

    @Test
    public void shouldFailValidationOfDateWhenDayDoesNotExist() throws Exception {
        //Given
        String date = "2016-11-31";

        //When
        boolean valid = underTest.isValid(date, null);

        //Then
        assertThat(valid, is(false));
    }

    @Test
    public void shouldFailValidationOfDateWhenDayGreaterThan31() throws Exception {
        //Given
        String date = "2016-11-32";

        //When
        boolean valid = underTest.isValid(date, null);

        //Then
        assertThat(valid, is(false));
    }
}