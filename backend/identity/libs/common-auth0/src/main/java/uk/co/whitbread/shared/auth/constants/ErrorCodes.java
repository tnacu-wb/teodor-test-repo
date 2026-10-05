package uk.co.whitbread.shared.auth.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodes {

    RETRIEVE_TOKEN_ERROR_CODE("7001"),
    USER_ALREADY_EXISTS_ERROR_CODE("7003"),
    RETRIEVE_USERS_EMAIL_ERROR_CODE("7004"),
    UPDATE_USER_EMAIL_ERROR_CODE("7005"),
    INVALID_LOGIN_ERROR_CODE("025");

    private String code;
}
