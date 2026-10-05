package uk.co.whitbread.company.domain.model.out;

public record EmergencyReportResult(
    String reportName,
    String fileName,
    String downloadUrl) {

}
