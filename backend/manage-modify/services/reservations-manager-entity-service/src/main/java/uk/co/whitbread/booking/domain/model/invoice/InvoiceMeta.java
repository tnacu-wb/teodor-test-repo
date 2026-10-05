package uk.co.whitbread.booking.domain.model.invoice;

import lombok.Builder;

@Builder
public record InvoiceMeta(
    String invoiceNumber,
    String issuedDate,
    String hotelId
) {}
