package uk.co.whitbread.piba.registration.validation;

import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.api.validation.WorldLineResponseValidator;
import uk.co.whitbread.piba.registration.exception.PibaRegistrationException;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponse;

import java.util.Optional;

@Component
public class WorldLineRegistrationResponseValidator extends WorldLineResponseValidator {
    public static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";

    public void validate(RegistrationGetInfoResponse response) {
        var validInnerResponse = Optional.ofNullable(response)
                .map(RegistrationGetInfoResponse::getResponse)
                .orElseThrow(() -> new PibaRegistrationException(ERROR_EMPTY_RESPONSE));
        validate(validInnerResponse);
    }

    public void validate(RegistrationAuthenticateResponse response) {
        var validInnerResponse = Optional.ofNullable(response)
                .map(RegistrationAuthenticateResponse::getResponse)
                .orElseThrow(() -> new PibaRegistrationException(ERROR_EMPTY_RESPONSE));
        validate(validInnerResponse);
    }

    public void validate(RegistrationSubmitResponse response) {
        var validInnerResponse = Optional.ofNullable(response)
                .map(RegistrationSubmitResponse::getResponse)
                .orElseThrow(() -> new PibaRegistrationException(ERROR_EMPTY_RESPONSE));
        validate(validInnerResponse);
    }
}

