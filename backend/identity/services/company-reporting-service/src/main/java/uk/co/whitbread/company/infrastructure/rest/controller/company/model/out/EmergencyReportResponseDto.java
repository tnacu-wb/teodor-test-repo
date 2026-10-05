package uk.co.whitbread.company.infrastructure.rest.controller.company.model.out;

public record EmergencyReportResponseDto(
    String reportName,
    String fileName,
    String downloadUrl) {

}
