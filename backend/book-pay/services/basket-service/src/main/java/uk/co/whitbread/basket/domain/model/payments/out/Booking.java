package uk.co.whitbread.basket.domain.model.payments.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

  private String channel;
  private String journey;
  private String type;
  private String reference;
  private BusinessSite businessSite;
  private String arrivalDate;
  private String departureDate;
  private String language;
  private Guest leadGuest;
  private List<RoomType> rooms;
}
