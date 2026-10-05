package uk.co.whitbread.booking.domain.model.invoice;

import java.util.List;
import lombok.Builder;

@Builder
public record InvoiceDownloadResponse(
    List<InvoiceDownload> invoices
) {}

