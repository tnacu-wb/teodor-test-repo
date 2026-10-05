package uk.co.whitbread.basket.domain.model.payments.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class Booking implements SelfValidation<Booking> {

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

  public Booking(String channel, String journey, String type, String reference, String bookingReference,
      BusinessSite businessSite, String arrivalDate, String departureDate, String language,
      Guest leadGuest, List<RoomType> rooms) {
    this.channel = channel;
    this.journey = journey;
    this.type = type;
    this.reference = reference;
    this.bookingReference = bookingReference;
    this.businessSite = businessSite;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.language = language;
    this.leadGuest = leadGuest;
    this.rooms = rooms;
    this.validateSelf();
  }
}
