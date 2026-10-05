package uk.co.whitbread.company.infrastructure.rest.controller.company.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.validation.DateFormat;

public record ManagementInformationRequestDto(

    @NotEmpty
    @DateFormat
    @Valid
    String fromDate,
    @NotEmpty
    @DateFormat
    @Valid
    String toDate,
    @NotNull
    Boolean showQnAcolumns,

    @NotNull
    @Pattern(regexp = "EN|DE", flags = Pattern.Flag.CASE_INSENSITIVE)
    String language) {
}

