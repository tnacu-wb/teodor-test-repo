package uk.co.whitbread.hotel.register.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterAccountRequest {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Firstname must not be blank")
    private String firstName;

    @NotBlank(message = "Lastname must not be blank")
    private String lastName;

    private UpdatePreferencesRequest updatePreferencesRequest;
}