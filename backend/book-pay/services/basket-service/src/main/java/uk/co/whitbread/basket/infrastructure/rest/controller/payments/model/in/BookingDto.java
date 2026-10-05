package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

  @NotEmpty
  private String channel;
  @NotEmpty
  private String journey;
  @NotEmpty
  private String type;
  private String reference;
  private String bookingReference;
  @NotNull
  private BusinessSiteDto businessSite;
  private String arrivalDate;
  private String departureDate;
  private String language;
  private GuestDto leadGuest;
  private List<RoomTypeDto> rooms;
}
