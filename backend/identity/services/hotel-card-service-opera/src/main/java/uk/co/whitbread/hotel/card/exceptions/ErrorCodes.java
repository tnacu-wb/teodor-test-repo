package uk.co.whitbread.hotel.card.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodes {

    VALIDATION_ERROR("600"),
    CONTEXT_ACTION_ERROR("601"),
    AUTHORISATION_ERROR("602"),
    NOT_FOUND_ERROR("603"),
    AUTHENTICATION_ERROR("604"),
    NOT_IMPLEMENTED("605"),
    ACCESS_DENIED_NOT_WHITELISTED("606"),
    SYSTEM_ERROR("607"),
    SCHEMA_ERROR("608");
    
    private final String code;

}
