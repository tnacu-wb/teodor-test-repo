package uk.co.whitbread.hotel.account.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodes {

    INVALID_LOGIN_ERROR_CODE("025");

    private final String code;

}