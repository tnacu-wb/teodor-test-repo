package uk.co.whitbread.piba.account.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodes {

    INVALID_COMPANY_EMPLOYEE_DETAILS("028");
    
    private final String code;

}
