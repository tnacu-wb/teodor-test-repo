package uk.co.whitbread.hotel.account.model;

public record ValidateResetKeyResponse(boolean valid,
                                       String emailAddress) {
}
