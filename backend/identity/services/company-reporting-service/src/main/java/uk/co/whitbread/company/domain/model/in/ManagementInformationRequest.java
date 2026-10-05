package uk.co.whitbread.company.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ManagementInformationRequest(

    @NotEmpty
    String fromDate,

    @NotEmpty
    String toDate,

    @NotNull
    Boolean showQnAcolumns,

    @NotEmpty
    String companyId,

    @NotEmpty
    String accessContext,

    @NotEmpty
    String accessedBy,

    @NotNull
    String language) {

}
