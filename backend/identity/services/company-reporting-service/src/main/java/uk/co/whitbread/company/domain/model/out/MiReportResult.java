package uk.co.whitbread.company.domain.model.out;

public record MiReportResult(
    String reportName,
    String fileName,
    String downloadUrl) {

}
