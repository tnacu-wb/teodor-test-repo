package uk.co.whitbread.piba.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.validation.TransactionsCriteriaConstraint;


@Data
public class CustomerAccountTransactionsRequest {
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "92562846-62b5-42d8-b393-132a1d78fd1a")
    protected String tetheredUserGuid;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "778653")
    protected int schemeCustomerId;

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED, example = "GB", defaultValue = "GB")
    protected Scheme scheme;

    @TransactionsCriteriaConstraint
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected CustomerAccountTransactionsCriteria searchCriteria;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected PagingRequestWithoutSort pagingRequest;
}
