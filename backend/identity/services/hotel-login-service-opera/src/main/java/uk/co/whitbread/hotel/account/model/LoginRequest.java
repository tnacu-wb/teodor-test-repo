package uk.co.whitbread.hotel.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Data
public class LoginRequest {

    @Email
    @NotEmpty
    @Schema(required = true, example = "user@google.com")
    private String username;

    @NotEmpty
    @Schema(required = true, example = "Str0ngPa$$w0rd")
    private String password;

    @Override
    public String toString() {
        return String.format("LoginRequest{username='%s'}", username);
    }
}
