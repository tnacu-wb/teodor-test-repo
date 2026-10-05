package uk.co.whitbread.piba.registration.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class RegistrationInfoRequest {

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "A6ZN-2AZZ-Z2T6-A3YG")
    private String registrationCode;

}
