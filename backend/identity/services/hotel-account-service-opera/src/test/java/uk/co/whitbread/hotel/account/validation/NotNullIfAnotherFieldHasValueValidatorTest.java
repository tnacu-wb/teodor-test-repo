package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.account.model.BBStaysRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotNullIfAnotherFieldHasValueValidatorTest {

    private final NotNullIfAnotherFieldHasValueValidator notNullIfAnotherFieldHasValueValidator = 
        new NotNullIfAnotherFieldHasValueValidator();

    private NotNullIfAnotherFieldHasValue notNullIfAnotherFieldHasValue;

    @BeforeEach
    void setUp() {

        notNullIfAnotherFieldHasValue = mock(NotNullIfAnotherFieldHasValue.class);
        when(notNullIfAnotherFieldHasValue.fieldName()).thenReturn("business");
        when(notNullIfAnotherFieldHasValue.fieldValue()).thenReturn("true");
        when(notNullIfAnotherFieldHasValue.dependFieldName()).thenReturn("employeeId");
    }

    @Test
    void validRequest_whenDependentFieldIsNotNullAndParentFieldHasCorrespondingValue() {
        notNullIfAnotherFieldHasValueValidator.initialize(notNullIfAnotherFieldHasValue);

        BBStaysRequest bbStaysRequest = new BBStaysRequest();
        bbStaysRequest.setEmployeeId("employeeId");
        bbStaysRequest.setBusiness(true);

        assertTrue(notNullIfAnotherFieldHasValueValidator.isValid(bbStaysRequest, null));
    }

    @Test
    void validRequest_whenDependentFieldIsNullAndParentFieldDoesNotHaveCorrespondingValue() {
        notNullIfAnotherFieldHasValueValidator.initialize(notNullIfAnotherFieldHasValue);

        BBStaysRequest bbStaysRequest = new BBStaysRequest();
        bbStaysRequest.setBusiness(false);

        assertTrue(notNullIfAnotherFieldHasValueValidator.isValid(bbStaysRequest, null));
    }

    @Test
    void validRequest_whenObjectIsNull() {
        notNullIfAnotherFieldHasValueValidator.initialize(notNullIfAnotherFieldHasValue);
        assertTrue(notNullIfAnotherFieldHasValueValidator.isValid(null, null));
    }

    @Test
    void inValidRequest_whenDependentFieldIsNullAndParentFieldHasCorrespondingValue() {

        ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext nodeBuilderDefinedContext = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext.class);
        when(constraintViolationBuilder.addNode(anyString())).thenReturn(nodeBuilderDefinedContext);
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilder);

        notNullIfAnotherFieldHasValueValidator.initialize(notNullIfAnotherFieldHasValue);

        BBStaysRequest bbStaysRequest = new BBStaysRequest();
        bbStaysRequest.setBusiness(true);

        assertFalse(notNullIfAnotherFieldHasValueValidator.isValid(bbStaysRequest, constraintValidatorContext));

        verify(constraintValidatorContext).disableDefaultConstraintViolation();
        verify(constraintViolationBuilder).addNode(any());
        verify(nodeBuilderDefinedContext).addConstraintViolation();
    }
}
