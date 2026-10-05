package uk.co.whitbread.company.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record EmergencyReportRequest(

    @NotEmpty
    String fromDate,

    @NotEmpty
    String toDate,

    @NotEmpty
    String companyId,

    @NotEmpty
    String accessContext,

    @NotEmpty
    String accessedBy) {

}
