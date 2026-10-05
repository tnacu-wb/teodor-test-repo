package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.out;

public record InvoiceMetaDto(
    String invoiceNumber,
    String issuedDate,
    String hotelId
) {}

