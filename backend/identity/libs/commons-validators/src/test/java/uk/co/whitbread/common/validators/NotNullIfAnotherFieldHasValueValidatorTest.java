package uk.co.whitbread.common.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.common.validators.testData.model.DummyObject;

import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotNullIfAnotherFieldHasValueValidatorTest {

    private NotNullIfAnotherFieldHasValueValidator notNullIfAnotherFieldHasValueValidator = new NotNullIfAnotherFieldHasValueValidator();

    private NotNullIfAnotherFieldHasValue notNullIfAnotherFieldHasValue;

    @BeforeEach
    public void setUp() {

        notNullIfAnotherFieldHasValue = mock(NotNullIfAnotherFieldHasValue.class);
        when(notNullIfAnotherFieldHasValue.fieldName()).thenReturn("dummy2");
        when(notNullIfAnotherFieldHasValue.fieldValue()).thenReturn("value2");
        when(notNullIfAnotherFieldHasValue.dependFieldName()).thenReturn("dummy3");

    }


    @Test
    public void validRequest_whenDependentFieldIsNotNullAndParentFieldHasCorrespondingValue() {
        notNullIfAnotherFieldHasValueValidator.initialize(notNullIfAnotherFieldHasValue);

        DummyObject dummyObject = new DummyObject();
        dummyObject.setDummy1("dummy1");
        dummyObject.setDummy2("value2");
        dummyObject.setDummy2("dummy2");
        assertTrue(notNullIfAnotherFieldHasValueValidator.isValid(dummyObject, null));

    }

    @Test
    public void validRequest_whenDependentFieldIsNullAndParentFieldDoesNotHaveCorrespondingValue() {
        notNullIfAnotherFieldHasValueValidator.initialize(notNullIfAnotherFieldHasValue);
        DummyObject dummyObject = new DummyObject();
        dummyObject.setDummy1("dummy1");
        assertTrue(notNullIfAnotherFieldHasValueValidator.isValid(dummyObject, null));
    }


    @Test
    public void validRequest_whenObjectIsNull() {
        notNullIfAnotherFieldHasValueValidator.initialize(notNullIfAnotherFieldHasValue);
        assertTrue(notNullIfAnotherFieldHasValueValidator.isValid(null, null));
    }

    @Test
    public void inValidRequest_whenDependentFieldIsNullAndParentFieldHasCorrespondingValue() {

        ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderDefinedContext = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
        when(constraintViolationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilderDefinedContext);
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilder);

        notNullIfAnotherFieldHasValueValidator.initialize(notNullIfAnotherFieldHasValue);

        DummyObject dummyObject = new DummyObject();
        dummyObject.setDummy2("value2");

        assertFalse(notNullIfAnotherFieldHasValueValidator.isValid(dummyObject, constraintValidatorContext));

        verify(constraintValidatorContext).disableDefaultConstraintViolation();
        verify(constraintViolationBuilder).addPropertyNode(any());
        verify(nodeBuilderDefinedContext).addConstraintViolation();
    }


}