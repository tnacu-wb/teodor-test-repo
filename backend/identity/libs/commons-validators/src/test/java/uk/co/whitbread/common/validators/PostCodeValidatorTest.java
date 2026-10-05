package uk.co.whitbread.common.validators;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class PostCodeValidatorTest {

    private ConstraintValidatorContext constraintValidatorContext;
    private PostCodeValidator postCodeValidator;


    @BeforeEach
    public void setup() {
        constraintValidatorContext = mock(ConstraintValidatorContext.class);
        postCodeValidator = new PostCodeValidator();
        postCodeValidator.initialize(null);
    }


    @Test
    public void postCodeIsValid() {
        assertTrue(postCodeValidator.isValid("A1234", constraintValidatorContext));
    }

    @Test
    public void postCodeIsValid_nullValue() {
        assertTrue(postCodeValidator.isValid(null, constraintValidatorContext));
    }


    @Test
    public void postCodeIsNotValid_missingLetter() {
        boolean isValid = postCodeValidator.isValid("1234", constraintValidatorContext);
        assertFalse(isValid);
    }

}

