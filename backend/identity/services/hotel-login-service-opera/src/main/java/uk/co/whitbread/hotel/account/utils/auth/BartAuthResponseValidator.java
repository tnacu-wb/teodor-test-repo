package uk.co.whitbread.hotel.account.utils.auth;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.co.whitbread.bart.auth0.api.ErrorDetails2;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse2;
import uk.co.whitbread.bart.booking.api.ClearSessionRequestResponse;
import uk.co.whitbread.bart.booking.api.ClearSessionResponse;
import uk.co.whitbread.bart.business.api.Error;
import uk.co.whitbread.bart.business.api.UserLoginResponse;
import uk.co.whitbread.bart.business.api.UserLoginResponse2;
import uk.co.whitbread.bart.common.ErrorDetails;
import uk.co.whitbread.bart.registeredguest.api.ForgottenPasswordRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.ForgottenPasswordResponse;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginResponse;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static uk.co.whitbread.bart.util.Utils.convertErrorDetails;

@Component
public class BartAuthResponseValidator {

    private static final String ERROR_EMPTY_RESPONSE = "Bart returned empty response.";
    private static final String UNSUCCESSFUL_ERROR_MESSAGE = "Reset password attempt was not successful";

    public Optional<String> validate(ForgottenPasswordRequestResponse response) {
        if (response == null || response.getForgottenPasswordRequestResult() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }

        ForgottenPasswordResponse result = response.getForgottenPasswordRequestResult();
        String resultCodeMessage = checkErrorMessage(result.getErrorDetail());
        if (resultCodeMessage != null) {
            return Optional.of(resultCodeMessage);
        }

        String resultMessage = checkErrorCode(result.getForgottenPasswordError());
        if (resultMessage != null) {
            return Optional.of(resultMessage);
        }

        if (!response.getForgottenPasswordRequestResult().isSuccess()) {
            return Optional.of(UNSUCCESSFUL_ERROR_MESSAGE);
        }

        return Optional.empty();
    }

    public Optional<String> validate(UserLoginResponse response) {
        if (response == null || response.getUserLoginResult() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }

        UserLoginResponse2 result = response.getUserLoginResult();
        if (result.getErrors() != null && !result.getErrors().getError().isEmpty()){
            Error error = result.getErrors().getError().get(0);

            if (StringUtils.hasLength(error.getErrorDescription())){
                    return Optional.of(error.getErrorDescription());
            }
            else {
                return Optional.of(error.getErrorCode());
            }
        }

        return Optional.empty();
    }

    public Optional<String> validate(RegisteredGuestLoginRequestResponse response) {
        if (response == null || response.getRegisteredGuestLoginRequestResult() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }

        RegisteredGuestLoginResponse result = response.getRegisteredGuestLoginRequestResult();
        String resultCodeMessage = checkErrorMessage(result.getErrorDetail());
        if (resultCodeMessage != null) {
            return Optional.of(resultCodeMessage);
        }

        return ofNullable(checkErrorCode(result.getLoginRegisteredGuestError()));
    }

    public Optional<String> validate(InitSessionResponse response) {
        if (response == null || response.getInitSessionResult() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }

        InitSessionResponse2 result = response.getInitSessionResult();
        String resultCodeMessage = checkErrorMessage(result.getErrorDetail());
        if (resultCodeMessage != null) {
            return Optional.of(resultCodeMessage);
        }

        return ofNullable(checkErrorCode(result.getInitError()));
    }

    public Optional<String> validate(uk.co.whitbread.bart.business.auth0.api.InitSessionResponse response) {
        if (response == null || response.getInitSessionResult() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }

        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse2 result = response.getInitSessionResult();
        ErrorDetails2 errorDetails = new ErrorDetails2();
        if(result.getErrors() != null && !result.getErrors().getError().isEmpty()) {
            errorDetails.setErrorMessage(result.getErrors().getError().get(0).getErrorDescription());
        }
        String resultCodeMessage = checkErrorMessage(errorDetails);
        if (resultCodeMessage != null) {
            return Optional.of(resultCodeMessage);
        }

        return Optional.empty();
    }

    public Optional<String> validate(ClearSessionRequestResponse response) {
        if (response == null || response.getClearSessionRequestResult() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }

        ClearSessionResponse result = response.getClearSessionRequestResult();
        String resultCodeMessage = checkErrorMessage(result.getErrorDetail());
        if (resultCodeMessage != null) {
            return Optional.of(resultCodeMessage);
        }

        return ofNullable(checkErrorCode(result.getClearSessionError()));
    }

    private String checkErrorCode(String validationError) {
        return StringUtils.hasLength(validationError) ? validationError : null;
    }

    private String checkErrorMessage(Object errorDetailObject) {
        ErrorDetails errorDetail = convertErrorDetails(errorDetailObject);
        if (errorDetail == null) return null;
        String errorMessage = errorDetail.getErrorMessage();
        return StringUtils.hasLength(errorMessage) ? errorMessage : null;
    }

}
