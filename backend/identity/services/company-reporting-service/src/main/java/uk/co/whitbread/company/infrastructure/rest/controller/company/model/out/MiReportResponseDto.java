package uk.co.whitbread.company.infrastructure.rest.controller.company.model.out;

public record MiReportResponseDto(
    String reportName,
    String fileName,
    String downloadUrl) {

}
