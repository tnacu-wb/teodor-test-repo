package uk.co.whitbread.ohip.domain.model.availability.in;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class MultiHotelAvailabilityRequest implements
    SelfValidation<MultiHotelAvailabilityRequest> {

  List<String> hotelIds;
  String arrivalDate;
  String departureDate;
  List<Integer> numberOfRooms;
  List<String> roomTypes;
  List<Integer> adults;
  List<Integer> children;
  List<Boolean> cotsRequired;
  String channel;
  String companyId;

  public MultiHotelAvailabilityRequest(List<String> hotelIds, String arrivalDate,
      String departureDate,
      List<Integer> numberOfRooms,
      List<String> roomTypes,
      List<Integer> adults,
      List<Integer> children,
      List<Boolean> cotsRequired,
      String channel,
      String companyId) {
    this.hotelIds = hotelIds;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.numberOfRooms = numberOfRooms;
    this.roomTypes = roomTypes;
    this.adults = adults;
    this.children = children;
    this.cotsRequired = cotsRequired;
    this.channel = channel;
    this.companyId = companyId;
    this.validateSelf();
  }


}
