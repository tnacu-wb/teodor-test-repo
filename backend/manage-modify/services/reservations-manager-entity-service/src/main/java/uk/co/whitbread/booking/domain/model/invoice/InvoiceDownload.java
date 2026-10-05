package uk.co.whitbread.booking.domain.model.invoice;

import lombok.Builder;

@Builder
public record InvoiceDownload(
    String bookingRef,
    String url,
    String expiresAt,
    String fileName,
    String mimeType,
    Language language,
    InvoiceMeta invoiceMeta
) {}

