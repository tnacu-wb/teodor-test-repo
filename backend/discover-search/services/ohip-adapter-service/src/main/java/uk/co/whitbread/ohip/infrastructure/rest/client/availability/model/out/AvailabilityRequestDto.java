package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRuleResponseDto;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityRequestDto {

  private String hotelId;
  private String ratePlanCode;
  private String ratePlanSet;
  private List<String> roomTypes;
  private String roomStayStartDate;
  private String roomStayEndDate;
  private Integer roomStayQuantity;
  private List<Integer> adults;
  private List<Integer> children;
  private List<Boolean> cotsRequired;
  private String channel;
  private String subchannel;
  private String language;
  private String companyId;
  private String promotionCode;
  private Integer limit = 20;
  private List<RoomSubstitutionRuleResponseDto> roomSubstitutions;
}
