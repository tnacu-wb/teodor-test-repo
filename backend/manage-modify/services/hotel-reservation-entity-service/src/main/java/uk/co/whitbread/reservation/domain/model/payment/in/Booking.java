package uk.co.whitbread.reservation.domain.model.payment.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

  @NotEmpty
  private String channel;
  @NotEmpty
  private String journey;
  @NotEmpty
  private String type;
  private String reference;
  private String bookingReference;
  @NotNull
  private BusinessSite businessSite;
  private String arrivalDate;
  private String departureDate;
  private String language;
  private Guest leadGuest;
  private List<RoomType> rooms;

}
