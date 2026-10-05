package uk.co.whitbread.reservation.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@EqualsAndHashCode(callSuper = false)
@ValidAvailabilityDates
public class HotelAvailabilityRequest implements SelfValidation<HotelAvailabilityRequest> {

  @NotNull
  String hotelId;
  @NotEmpty
  String arrivalDate;
  @NotEmpty
  String departureDate;
  @NotEmpty
  List<String> roomTypes;
  @NotEmpty
  List<Integer> adultsNumber;
  List<Integer> childrenNumber;
  List<Boolean> cotsRequired;
  String companyId;
  String channel;
  String subchannel;
  String language;
  List<String> ratePlanCodes;

  @SuppressWarnings("squid:S107")
  public HotelAvailabilityRequest(
      String hotelId, String arrivalDate, String departureDate,
      List<String> roomTypes, List<Integer> adultsNumber,
      List<Integer> childrenNumber,
      List<Boolean> cotsRequired, String companyId, String channel,
      String subchannel, String language, List<String> ratePlanCodes) {
    this.hotelId = hotelId;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.roomTypes = roomTypes;
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
    this.cotsRequired = cotsRequired;
    this.companyId = companyId;
    this.channel = channel;
    this.subchannel = subchannel;
    this.language = language;
    this.ratePlanCodes = ratePlanCodes;
    this.validateSelf();
  }
}
