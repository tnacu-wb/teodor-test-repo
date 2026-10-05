package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentInfoQueryParamsDto(
    @Parameter(in = ParameterIn.QUERY, name = "accountId",
        example = "1234567",
        required = true, schema = @Schema(type = "string"))
    @NotBlank
    String accountId,
    @Parameter(in = ParameterIn.QUERY, name = "tetherUserGuid",
        example = "17b02526-a0bc-1111-90d7-fb7fdae31147",
        schema = @Schema(type = "string"))
    String tetheredUserGuid,
    @Parameter(in = ParameterIn.QUERY, name = "page",
        example = "1",
        required = true, schema = @Schema(type = "number"))
    @NotNull
    @Min(1)
    Integer page,
    @Parameter(in = ParameterIn.QUERY, name = "size",
        example = "1",
        required = true, schema = @Schema(type = "number"))
    @NotNull
    @Min(1)
    Integer size,
    @Parameter(in = ParameterIn.QUERY, name = "nonInvoiceOnly",
        example = "true",
        required = true, schema = @Schema(type = "boolean"))
    @NotNull
    Boolean nonInvoiceOnly) {

}
