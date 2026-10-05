package uk.co.whitbread.piba.registration.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
public class RegistrationAuthenticationRequest {
    @NotEmpty
    @Schema(example = "A6ZN-2AZZ-Z2T6-A3YG")
    private String registrationCode;

    @Valid
    private List<RegistrationAuthenticationAnswer> authenticationAnswers;
}
