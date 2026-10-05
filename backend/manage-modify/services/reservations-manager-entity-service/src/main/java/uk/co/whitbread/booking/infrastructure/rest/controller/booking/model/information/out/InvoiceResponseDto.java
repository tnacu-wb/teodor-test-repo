package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

public record InvoiceResponseDto(
    String reportName,
    String fileName,
    String downloadUrl) {
}
