package uk.co.whitbread.ohip.domain.model.availability.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class AvailabilitySearchCriteria implements SelfValidation<AvailabilitySearchCriteria> {

  private String hotelId;
  private String arrivalDate;
  private String departureDate;
  private List<String> roomTypes;
  private List<Integer> adults;
  private List<Integer> children;
  private List<Boolean> cotsRequired;
  private String channel;
  private String subchannel;
  private String language;
  private String companyId;
  private String ratePlanCode;
  private String promotionCode;

  public AvailabilitySearchCriteria(String hotelId, String arrivalDate,
                                    String departureDate, List<String> roomTypes,
                                    List<Integer> adults,
                                    List<Integer> children, List<Boolean> cotsRequired,
                                    String channel,
                                    String subchannel, String language, String companyId,
                                    String ratePlanCode, String promotionCode) {
    this.hotelId = hotelId;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.roomTypes = roomTypes;
    this.adults = adults;
    this.children = children;
    this.cotsRequired = cotsRequired;
    this.channel = channel;
    this.subchannel = subchannel;
    this.language = language;
    this.companyId = companyId;
    this.ratePlanCode = ratePlanCode;
    this.promotionCode = promotionCode;
    this.validateSelf();
  }
}
