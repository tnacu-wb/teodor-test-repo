package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

  private String channel;
  private String journey;
  private String type;
  private BusinessSiteDto businessSite;
  private String arrivalDate;
  private String departureDate;
  private String language;
  private GuestDto leadGuest;
  private List<RoomTypeDto> rooms;

}
