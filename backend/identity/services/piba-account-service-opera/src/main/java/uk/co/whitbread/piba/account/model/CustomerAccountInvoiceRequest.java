package uk.co.whitbread.piba.account.model;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.validation.InvoicesCriteriaConstraint;


@Data
@Schema(name = "CustomerAccountInvoiceRequest")
public class CustomerAccountInvoiceRequest {

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1a409f7d-a66c-4991-a424-97b7ce0e6cf9")
    protected String tetheredUserGuid;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example= "783582")
    protected int schemeCustomerId;

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED, defaultValue = "GB", allowableValues = "DE, GB")
    protected Scheme scheme;

    @InvoicesCriteriaConstraint
    protected CustomerAccountInvoiceCriteria searchCriteria;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    protected PagingRequestWithoutSort pagingRequest;
}
