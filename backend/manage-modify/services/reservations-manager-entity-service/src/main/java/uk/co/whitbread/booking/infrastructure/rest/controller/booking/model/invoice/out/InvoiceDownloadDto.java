package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.out;

import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.LanguageDto;

public record InvoiceDownloadDto(
    String bookingRef,
    String url,
    String expiresAt,
    String fileName,
    String mimeType,
    LanguageDto language,
    InvoiceMetaDto invoiceMeta
) {}

