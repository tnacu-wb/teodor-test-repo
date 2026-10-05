package uk.co.whitbread.hotel.register.exceptions.universal_login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Enum defining all Universal Login error codes with their associated HTTP status and titles.
 * This provides a centralized, type-safe way to manage error responses.
 */
@Getter
@RequiredArgsConstructor
public enum UniversalLoginErrorCode {

    VALIDATION_FAILED("UL-400-001", HttpStatus.BAD_REQUEST, "Universal Login: Validation Failed"),

    EXCEPTION_OCCURRED("UL-400-002", HttpStatus.BAD_REQUEST, "Universal Login: Exception Occurred"),

    INTERNAL_SERVER_ERROR("UL-500-001", HttpStatus.INTERNAL_SERVER_ERROR, "Universal Login: Internal Server Error");

    private final String code;
    private final HttpStatus httpStatus;
    private final String title;
}