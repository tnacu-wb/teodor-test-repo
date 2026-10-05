package uk.co.whitbread.hotel.account.model;

import lombok.Data;

@Data
public class LogoutResponse {
    private final boolean logoutSuccessful;

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "logoutSuccessful=" + logoutSuccessful +
                '}';
    }
}
