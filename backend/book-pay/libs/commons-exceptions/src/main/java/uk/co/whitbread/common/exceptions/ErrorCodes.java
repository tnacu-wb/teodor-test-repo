package uk.co.whitbread.common.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCodes {

    VALIDATION_ERROR_CODE("001"),
    REQUIRED_HEADER_MISSING_CODE("013"),
    UNCLASSIFIED_ERROR_CODE("999"),
    BART_GENERIC_ERROR_CODE("100"),
    AUTH0_GENERIC_ERROR_CODE("7000"),
    HTTP_METHOD_NOT_SUPPORTED("049"),
    NO_RESOURCE_FOUND_EXCEPTION("413"),
    NO_HANDLER_FOUND_EXCEPTION("414");

    private final String code;


}
