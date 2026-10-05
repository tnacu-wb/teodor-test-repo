package uk.co.whitbread.piba.account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;



@Data
public class AccountBalanceRequest {
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    @JsonProperty("companyId")
    private String companyID;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1111111111")
    @JsonProperty("employeeId")
    private String employeeID;
}
