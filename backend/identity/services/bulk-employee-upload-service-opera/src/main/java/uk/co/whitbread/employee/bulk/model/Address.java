package uk.co.whitbread.employee.bulk.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
