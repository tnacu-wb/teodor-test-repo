package uk.co.whitbread.ohip.domain.model.availability.in;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class AvailabilityByIdsSearchRequest
    implements SelfValidation<AvailabilityByIdsSearchRequest> {

  List<String> hotelIds;
  String arrivalDate;
  String departureDate;
  Integer numberOfRooms;
  List<String> roomTypes;
  List<Integer> adults;
  List<Integer> children;
  List<Boolean> cotsRequired;
  List<String> ratePlanCodes;
  String channel;
  String subchannel;
  String language;
  String globalCompanyId;
  List<String> negotiatedRateDisplaySets;
  List<RoomSubstitutionRuleResponse> roomSubstitutions;

  public AvailabilityByIdsSearchRequest(List<String> hotelIds, String arrivalDate,
                                        String departureDate, Integer numberOfRooms,
                                        List<String> roomTypes, List<Integer> adults,
                                        List<Integer> children, List<Boolean> cotsRequired,
                                        List<String> ratePlanCodes, String channel,
                                        String subchannel, String language, String globalCompanyId,
                                        List<String> negotiatedRateDisplaySets,
                                        List<RoomSubstitutionRuleResponse> roomSubstitutions) {
    this.hotelIds = hotelIds;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.numberOfRooms = numberOfRooms;
    this.roomTypes = roomTypes;
    this.adults = adults;
    this.children = children;
    this.cotsRequired = cotsRequired;
    this.ratePlanCodes = ratePlanCodes;
    this.channel = channel;
    this.subchannel = subchannel;
    this.language = language;
    this.globalCompanyId = globalCompanyId;
    this.negotiatedRateDisplaySets = negotiatedRateDisplaySets;
    this.roomSubstitutions = roomSubstitutions;
    this.validateSelf();
  }
}
