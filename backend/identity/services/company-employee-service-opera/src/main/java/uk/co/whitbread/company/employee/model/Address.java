package uk.co.whitbread.company.employee.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @NotEmpty
    @Schema(requiredMode = RequiredMode.REQUIRED, example = "120 Holborn")
    private String addressLine1;

    @Schema
    private String addressLine2;

    @Schema
    private String addressLine3;

    @Schema
    private String addressLine4;

    @Schema
    private String addressLine5;

    @NotEmpty
    @Schema(requiredMode = RequiredMode.REQUIRED , example = "EC1N 2TD")
    private String postCode;

    @NotEmpty
    @Pattern(regexp = "^[A-Z]{1,3}$", message = "Invalid country code")
    @Schema(requiredMode = RequiredMode.REQUIRED, example = "GB")
    private String country;
}
