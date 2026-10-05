package uk.co.whitbread.reservation.domain.model.availability.in;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;
import uk.co.whitbread.reservation.domain.model.validator.ValidAvailabilityDates;

@Data
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@ValidAvailabilityDates
public class HotelAvailabilityByIdsV2Request
    implements SelfValidation<HotelAvailabilityByIdsV2Request> {

  private BookingChannel bookingChannel;
  private List<String> hotelIds;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<Room> rooms;
  private Rate rates;
  private boolean vatNotRequired;
  private Boolean isOTA;

  public HotelAvailabilityByIdsV2Request(BookingChannel bookingChannel, List<String> hotelIds,
      LocalDate arrivalDate, LocalDate departureDate, List<Room> rooms, Rate rates,
      boolean vatNotRequired,  Boolean isOTA) {
    this.bookingChannel = bookingChannel;
    this.hotelIds = hotelIds;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.rooms = rooms;
    this.rates = rates;
    this.vatNotRequired = vatNotRequired;
    this.isOTA = isOTA;
    this.validateSelf();
  }
}
