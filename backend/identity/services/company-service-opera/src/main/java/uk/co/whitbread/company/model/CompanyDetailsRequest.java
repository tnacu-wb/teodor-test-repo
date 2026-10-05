package uk.co.whitbread.company.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CompanyDetailsRequest {

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "BCQR98267")
    private String sessionId;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "12345")
    private String companyId;
}
