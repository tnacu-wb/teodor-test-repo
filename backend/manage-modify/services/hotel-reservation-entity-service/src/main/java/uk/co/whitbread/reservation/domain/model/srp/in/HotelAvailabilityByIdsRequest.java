package uk.co.whitbread.reservation.domain.model.srp.in;

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
public class HotelAvailabilityByIdsRequest
    implements SelfValidation<HotelAvailabilityByIdsRequest> {

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

  @SuppressWarnings("squid:S107")
  public HotelAvailabilityByIdsRequest(
      List<String> hotelIds, String arrivalDate, String departureDate, List<String> roomTypes,
      List<Integer> adultsNumber, List<Integer> childrenNumber, List<Boolean> cotsRequired,
      List<String> ratePlanCodes, String channel, String subchannel, String language, String country,
      String globalCompanyId, List<String> negotiatedRateDisplaySets, boolean vatNotRequired,
      List<String> pmsRoomTypes) {
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
    this.validateSelf();
  }
}
