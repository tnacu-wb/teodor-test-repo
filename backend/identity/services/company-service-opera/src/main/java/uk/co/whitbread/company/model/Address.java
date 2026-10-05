package uk.co.whitbread.company.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "120 Holborn")
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
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "EC1N 2TD")
    private String postCode;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "GB")
    private String country;
}
