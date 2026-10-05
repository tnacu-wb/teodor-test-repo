package uk.co.whitbread.hotel.account.utils.account;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.bart.common.ErrorDetails;
import uk.co.whitbread.bart.registeredguest.api.ChangePasswordRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.ChangePasswordResponse;
import uk.co.whitbread.bart.registeredguest.api.FutureStaysRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.FutureStaysResponse2;

import java.util.Optional;

import static uk.co.whitbread.bart.util.Utils.convertErrorDetails;

@Component
public class BartResponseValidator {
    private static final String ERROR_EMPTY_RESPONSE = "Bart returned empty response.";
    private static final String UNSUCCESSFUL_ERROR_MESSAGE = "Change password attempt was not successful";

    public Optional<String> validate(ChangePasswordRequestResponse response) {

        if (response == null || response.getChangePasswordRequestResult() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }

        ChangePasswordResponse result = response.getChangePasswordRequestResult();
        String resultCodeMessage = checkErrorMessage(result.getErrorDetail());
        if (resultCodeMessage != null) {
            return Optional.of(resultCodeMessage);
        }

        String resultMessage = checkErrorCode(result.getPasswordChangedError());
        if (resultMessage != null) {
            return Optional.of(resultMessage);
        }

        if (!result.isPasswordChanged()) {
            return Optional.of(UNSUCCESSFUL_ERROR_MESSAGE);
        }

        return Optional.empty();
    }

    public Optional<String> validate(FutureStaysRequestResponse response) {
        if (response == null || response.getFutureStaysRequestResult() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }

        FutureStaysResponse2 result = response.getFutureStaysRequestResult();
        String resultCodeMessage = checkErrorMessage(result.getErrorDetail());
        if (resultCodeMessage != null) {
            return Optional.of(resultCodeMessage);
        }

        String resultMessage = checkErrorCode(result.getFutureStaysError());
        if (resultMessage != null) {
            return Optional.of(resultMessage);
        }

        return Optional.empty();
    }

    private String checkErrorCode(String validationError) {
        return StringUtils.isNotBlank(validationError) ? validationError : null;
    }

    private String checkErrorMessage(Object errorDetailObject) {
        ErrorDetails errorDetail = convertErrorDetails(errorDetailObject);
        if (errorDetail == null || StringUtils.isBlank(errorDetail.getErrorMessage())) {
            return null;
        }
        return errorDetail.getErrorMessage();
    }
}
