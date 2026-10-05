package uk.co.whitbread.piba.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.whitbread.piba.account.model.enums.Scheme;


@Data
@AllArgsConstructor
public class CustomerAccountCurrentBalancesRequest {
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "92562846-62b5-42d8-b393-132a1d78fd1a")
    protected String tetheredUserGuid;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "778653")
    protected int schemeCustomerId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected Scheme scheme;

}
