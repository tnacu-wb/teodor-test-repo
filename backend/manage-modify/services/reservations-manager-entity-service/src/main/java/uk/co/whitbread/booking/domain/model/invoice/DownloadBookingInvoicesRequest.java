package uk.co.whitbread.booking.domain.model.invoice;

import java.util.List;
import lombok.Builder;

@Builder
public record DownloadBookingInvoicesRequest(
    List<String> bookingRef,
    Language lang,
    String channel,
    String subChannel,
    String hotelBrand
) {}
