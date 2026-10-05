package uk.co.whitbread.ohip.domain.model.availability.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class AvailabilityByIdsSearchCriteria
    implements SelfValidation<AvailabilityByIdsSearchCriteria> {

  private List<String> hotelIds;
  private String arrivalDate;
  private String departureDate;
  private List<String> roomTypes;
  private List<Integer> adults;
  private List<Integer> children;
  private List<Boolean> cotsRequired;
  private List<String> ratePlanCodes;
  private String channel;
  private String subchannel;
  private String language;
  private String globalCompanyId;
  private List<String> negotiatedRateDisplaySets;
  private List<String> pmsRoomTypes;

  public AvailabilityByIdsSearchCriteria(List<String> hotelIds, String arrivalDate,
                                         String departureDate, List<String> roomTypes,
                                         List<Integer> adults, List<Integer> children,
                                         List<Boolean> cotsRequired, List<String> ratePlanCodes,
                                         String channel, String subchannel, String language,
                                         String globalCompanyId, List<String> negotiatedRateDisplaySets,
                                         List<String> pmsRoomTypes) {
    this.hotelIds = hotelIds;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
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
    this.pmsRoomTypes = pmsRoomTypes;
    this.validateSelf();
  }
}
