package uk.co.whitbread.payments.service.impl;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;

import jakarta.validation.Validator;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultValidationServiceTest {

    private DefaultValidationService defaultValidationService;
    private final Validator validatorMock = mock(Validator.class);

    @BeforeEach
    void setUp() {
        defaultValidationService = new DefaultValidationService(validatorMock);
    }

    @Test
    void testValidationPasses() {
        when(validatorMock.validate(any())).thenReturn(Collections.emptySet());
        defaultValidationService.validate("");
        verify(validatorMock, times(1)).validate(any());
    }

    @Test
    void testValidationFailureThrowsPaymentServiceException() {
        var constraintViolation = mock(ConstraintViolationImpl.class);
        var constraintViolation2 = mock(ConstraintViolationImpl.class);
        when(validatorMock.validate(any())).thenReturn(Set.of(constraintViolation, constraintViolation2));
        try {
            defaultValidationService.validate("");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof PaymentServiceException);
            var paymentServiceException = (PaymentServiceException) e;
            assertEquals(HttpStatus.BAD_REQUEST, paymentServiceException.getStatusCode());
            assertEquals(ErrorCodes.VALIDATION_ERROR, paymentServiceException.getErrorCode());
        }
    }
}