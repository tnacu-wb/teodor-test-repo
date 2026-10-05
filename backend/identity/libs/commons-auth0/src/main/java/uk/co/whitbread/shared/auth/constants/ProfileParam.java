package uk.co.whitbread.shared.auth.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProfileParam {
    EMAIL("name"),
    SESSION_ID("sessionId"),
    COMPANY_ID("companyId"),
    EMPLOYEE_ID("employeeId"),
    CUSTOMER_ACCOUNT_ID("https://premierinn.com/customerAccountId"),
    COMPANY_ACCOUNT_ID("https://premierinn.com/companyAccountId"),
    EMPLOYEE_ACCOUNT_ID("https://premierinn.com/employeeAccountId"),
    USER_EMAIL("https://premierinn.com/email"),
    CCUI_EMAIL("email"),
    BOOKING_FLOW("bookingFlow"),
    ACCESS_LEVEL("accessLevel"),
    ISSUED_AT("iat");

    private final String param;

}
