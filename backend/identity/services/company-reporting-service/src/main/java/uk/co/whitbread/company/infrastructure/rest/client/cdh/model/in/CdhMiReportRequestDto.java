package uk.co.whitbread.company.infrastructure.rest.client.cdh.model.in;

public record CdhMiReportRequestDto(

    String fromDate,
    String toDate,
    String accessContext,
    String accessedBy) {

}
