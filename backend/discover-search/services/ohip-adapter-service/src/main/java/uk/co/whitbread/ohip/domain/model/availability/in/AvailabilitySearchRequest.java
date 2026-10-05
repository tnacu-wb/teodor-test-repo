package uk.co.whitbread.ohip.domain.model.availability.in;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class AvailabilitySearchRequest implements SelfValidation<AvailabilitySearchRequest> {

  String hotelId;
  String arrivalDate;
  String departureDate;
  Integer numberOfRooms;
  String[] roomTypes;
  Integer[] adults;
  Integer[] children;
  Boolean[] cotsRequired;
  String channel;
  String subchannel;
  String language;
  String companyId;
  String ratePlanCode;

  public AvailabilitySearchRequest(String hotelId, String arrivalDate, String departureDate,
                                   Integer numberOfRooms, String[] roomTypes, Integer[] adults,
                                   Integer[] children,
                                   Boolean[] cotsRequired, String channel, String subchannel,
                                   String language, String companyId, String ratePlanCode) {
    this.hotelId = hotelId;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.numberOfRooms = numberOfRooms;
    this.roomTypes = roomTypes;
    this.adults = adults;
    this.children = children;
    this.cotsRequired = cotsRequired;
    this.channel = channel;
    this.subchannel = subchannel;
    this.language = language;
    this.companyId = companyId;
    this.ratePlanCode = ratePlanCode;
    this.validateSelf();
  }
}
