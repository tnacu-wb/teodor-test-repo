package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.out;

import java.util.List;

public record InvoiceDownloadResponseDto(
    List<InvoiceDownloadDto> invoices
) {}

