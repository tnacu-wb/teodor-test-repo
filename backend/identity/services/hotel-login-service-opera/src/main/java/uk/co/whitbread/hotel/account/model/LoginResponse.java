package uk.co.whitbread.hotel.account.model;

import lombok.Data;

@Data
public class LoginResponse {
    private String sessionId;
    private boolean loginSuccessful;

}
