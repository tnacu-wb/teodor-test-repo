package uk.co.whitbread.company.infrastructure.rest.controller.company.model.out;

public record ManagementReportResponseDto(

    String base64Report,
    String reportName,
    String fileName) {

}
