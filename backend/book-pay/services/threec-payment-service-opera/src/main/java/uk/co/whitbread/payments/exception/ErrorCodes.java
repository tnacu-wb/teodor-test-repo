package uk.co.whitbread.payments.exception;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    UNKNOWN_ERROR("TC0"),
    VALIDATION_ERROR("TC1"),
    PROVIDER_ACCOUNT_NOT_FOUND("TC2"),
    TEMPLATE_ERROR("TC3"),
    UNABLE_TO_PARSE_PROVIDER_RESPONSE("TC4"),
    ERROR_HANDLING_REQUEST("TC5"),
    PROVIDER_ERROR("TC6"),
    UNAUTHORISED("TC7"),
    PAYMENT_NOT_FOUND("TC8"),
    UNABLE_TO_REFUND("TC9"),
    THREEC_TIMEOUT("TC10"),
    SESSION_EXTENSION_ERROR("TC11"),
    CONNECTION_ERROR("TC12"),
    PAYPAL_ERROR("PAYPAL0"),
    PAYPAL_TIMEOUT("PAYPAL1"),
    PAYPAL_REFUSED("PAYPAL2"),;

    private final String errorCode;

    ErrorCodes(String errorCode) {
        this.errorCode = errorCode;
    }
}
