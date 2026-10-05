package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailMessageType {

    ACCOUNT_REGISTRATION("account_registration"),
    CHANGE_PASSWORD_CONFIRMATION("change_password_confirmation"),
    RESET_EMAIL_BY_CODE("reset_email_by_code"),
    BLOCKED_ACCOUNT("blocked_account");

    @JsonValue
    private final String value;

    public static EmailMessageType fromValue(String value) {
        for (EmailMessageType type : EmailMessageType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + value);
    }
}

