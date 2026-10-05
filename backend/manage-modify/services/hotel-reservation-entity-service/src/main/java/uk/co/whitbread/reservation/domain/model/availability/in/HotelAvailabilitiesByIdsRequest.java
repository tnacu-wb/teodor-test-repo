package uk.co.whitbread.reservation.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class HotelAvailabilitiesByIdsRequest
    implements SelfValidation<HotelAvailabilitiesByIdsRequest> {
  @NotNull
  List<String> hotelIds;
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
  List<String> ratePlanCodes;
  String channel;
  String subchannel;
  String language;
  String country;
  String globalCompanyId;
  List<String> negotiatedRateDisplaySets;
  boolean vatNotRequired;
  List<String> pmsRoomTypes;
  Boolean isOTA;

  @SuppressWarnings("squid:S107")
  public HotelAvailabilitiesByIdsRequest(
      List<String> hotelIds, String arrivalDate, String departureDate, List<String> roomTypes,
      List<Integer> adultsNumber, List<Integer> childrenNumber, List<Boolean> cotsRequired,
      List<String> ratePlanCodes, String channel, String subchannel, String language, String country,
      String globalCompanyId, List<String> negotiatedRateDisplaySets, boolean vatNotRequired,
      List<String> pmsRoomTypes, Boolean isOTA) {
    this.hotelIds = hotelIds;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.roomTypes = roomTypes;
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
    this.cotsRequired = cotsRequired;
    this.ratePlanCodes = ratePlanCodes;
    this.channel = channel;
    this.subchannel = subchannel;
    this.language = language;
    this.country = country;
    this.globalCompanyId = globalCompanyId;
    this.negotiatedRateDisplaySets = negotiatedRateDisplaySets;
    this.vatNotRequired = vatNotRequired;
    this.pmsRoomTypes = pmsRoomTypes;
    this.isOTA = isOTA;
    this.validateSelf();
  }
}
