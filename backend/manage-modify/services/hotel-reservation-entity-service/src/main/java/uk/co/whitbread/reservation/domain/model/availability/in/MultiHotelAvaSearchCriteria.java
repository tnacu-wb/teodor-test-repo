package uk.co.whitbread.reservation.domain.model.availability.in;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class MultiHotelAvaSearchCriteria implements SelfValidation<MultiHotelAvaSearchCriteria> {

  List<String> hotelIds;
  String arrivalDate;
  String departureDate;
  String channel;
  List<Integer> numberOfRooms;
  List<String> roomTypes;
  List<Integer> adults;
  List<Integer> children;
  List<Boolean> cotsRequired;

  public MultiHotelAvaSearchCriteria(
      List<String> hotelIds, String arrivalDate,
      String departureDate, String channel, List<Integer> numberOfRooms, List<String> roomTypes,
      List<Integer> adults, List<Integer> children, List<Boolean> cotsRequired) {
    this.hotelIds = hotelIds;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.channel = channel;
    this.numberOfRooms = numberOfRooms;
    this.roomTypes = roomTypes;
    this.adults = adults;
    this.children = children;
    this.cotsRequired = cotsRequired;
    this.validateSelf();
  }
}
