package uk.co.whitbread.business.tether.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodes {

    INVALID_COMPANY_EMPLOYEE_DETAILS("028"),
    INVALID_ACCOUNT_OR_CARD_VAL("029");
    
    private final String code;

}
