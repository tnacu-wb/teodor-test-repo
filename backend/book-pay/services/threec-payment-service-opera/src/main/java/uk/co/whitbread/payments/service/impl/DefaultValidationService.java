package uk.co.whitbread.payments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.service.ValidationService;

import jakarta.validation.Validator;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultValidationService implements ValidationService {

    private final Validator validator;

    @Override
    public void validate(Object request) {
        var errors = validator.validate(request);
        if (errors.size() > 0) {
            log.error("Validation errors {}", errors.toString());
            throw new PaymentServiceException(HttpStatus.BAD_REQUEST, errors.toString(), ErrorCodes.VALIDATION_ERROR);
        }
    }
}
