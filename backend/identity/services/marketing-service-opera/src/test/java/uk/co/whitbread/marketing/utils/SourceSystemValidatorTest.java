package uk.co.whitbread.marketing.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.co.whitbread.marketing.model.SourceChannel;
import uk.co.whitbread.marketing.model.SourceLocale;
import uk.co.whitbread.marketing.model.UserJourney;
import uk.co.whitbread.marketing.validations.SourceSystemValidator;
import uk.co.whitbread.marketing.validations.ValidateSourceSystem;

public class SourceSystemValidatorTest {

    private final SourceSystemValidator sourceSystemValidator = new SourceSystemValidator();
    private static ValidateSourceSystem validateSourceSystem;
    private static ConstraintValidatorContext constraintValidatorContext;

    @BeforeAll
    public static void setUp() {
        validateSourceSystem = mock(ValidateSourceSystem.class);
        constraintValidatorContext = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder = mock(
                ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(any()))
                .thenReturn(constraintViolationBuilder);
    }

    @Test
    public void validateUserJourney() {
        when(validateSourceSystem.enumClass()).thenAnswer(setupDummyListAnswer(UserJourney.class));
        sourceSystemValidator.initialize(validateSourceSystem);

        assertTrue(sourceSystemValidator.isValid("SIGNUP", constraintValidatorContext));
        assertTrue(sourceSystemValidator.isValid("PERMISSIONCENTRE", constraintValidatorContext));
        assertTrue(sourceSystemValidator.isValid("BOOKINGFLOW", constraintValidatorContext));
        assertTrue(sourceSystemValidator.isValid("NEWSLETTERSIGNUP", constraintValidatorContext));
        assertTrue(sourceSystemValidator.isValid("ACTIVATE", constraintValidatorContext));

        assertFalse(sourceSystemValidator.isValid("signUp", constraintValidatorContext));
        assertFalse(sourceSystemValidator.isValid("fggdddikjg", constraintValidatorContext));
    }

    @Test
    public void validateChannel() {
        when(validateSourceSystem.enumClass()).thenAnswer(setupDummyListAnswer(SourceChannel.class));
        sourceSystemValidator.initialize(validateSourceSystem);
        assertTrue(sourceSystemValidator.isValid("WEB", constraintValidatorContext));
        assertTrue(sourceSystemValidator.isValid("BB", constraintValidatorContext));
        assertTrue(sourceSystemValidator.isValid("APPS_IOS", constraintValidatorContext));
        assertTrue(sourceSystemValidator.isValid("APPS_ANDROID", constraintValidatorContext));
        

        assertFalse(sourceSystemValidator.isValid("APPS_ANdroID", constraintValidatorContext));
        assertFalse(sourceSystemValidator.isValid("sdfsd", constraintValidatorContext));
    }

    @Test
    public void missingOneMandatoryFieldShouldBeInvalid() {
        when(validateSourceSystem.enumClass()).thenAnswer(setupDummyListAnswer(SourceLocale.class));
        sourceSystemValidator.initialize(validateSourceSystem);

        assertTrue(sourceSystemValidator.isValid("UK", constraintValidatorContext));
        assertTrue(sourceSystemValidator.isValid("DE", constraintValidatorContext));
        
        assertFalse(sourceSystemValidator.isValid("asdkjfslkjdf", constraintValidatorContext));

    }

    private Answer<Class<? extends Enum<?>>> setupDummyListAnswer(Class<? extends Enum<?>> classObj) {

        Answer<Class<? extends Enum<?>>> answer = new Answer<>() {
            @Override
            public Class<? extends Enum<?>> answer(InvocationOnMock invocation) throws Throwable {
                return classObj;
            }
        };
        return answer;
    }

}