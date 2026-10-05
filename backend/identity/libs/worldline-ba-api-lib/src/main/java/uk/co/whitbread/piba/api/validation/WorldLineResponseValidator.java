package uk.co.whitbread.piba.api.validation;

import org.springframework.util.StringUtils;
import uk.co.whitbread.piba.api.exception.PibaException;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;
import worldline.mst.bsm.api.b2b.pi.data.ResponseType;

import java.util.List;
import java.util.stream.Collectors;

public class WorldLineResponseValidator {
    public static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";

    public void validate(ResponseType response) {
        if (response == null ) {
            throw new PibaException(ERROR_EMPTY_RESPONSE);
        }

        String resultCodeMessage = checkErrorMessage(response.getErrors());
        if (resultCodeMessage != null) {
            throw new PibaException(resultCodeMessage).withErrorCode(response.getResultCode());
        }

    }

    private String checkErrorCode(String validationError) {
        return StringUtils.hasLength(validationError) ? validationError : null;
    }

    private String checkErrorMessage(List<ErrorInfoType> errorsItem) {

        String errorsMessage = errorsItem.stream()
                .map(ErrorInfoType::getErrorCode)
                .collect(Collectors.joining(","));

        return checkErrorCode(errorsMessage);
    }
}

