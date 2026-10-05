package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.LanguageDto;

public record DownloadBookingInvoicesRequestDto(
    @NotEmpty List<String> bookingRef,
    @NotNull LanguageDto lang,
    @NotNull BookingChannelType channel,
    String subChannel,
    String hotelBrand
) {

  public enum BookingChannelType {
    PI,
    BB
  }
}

