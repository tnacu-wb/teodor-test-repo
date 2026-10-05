package uk.co.whitbread.piba.registration.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RegistrationSubmitRequest {
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "A6ZN-2AZZ-Z2T6-A3YG")
    private String registrationCode;

    @Valid
    private List<RegistrationAuthenticationAnswer> authenticationAnswers;

    @Valid
    @NotNull
    private RegistrationDetails registrationDetails;

    private boolean innBusiness = false;
}
