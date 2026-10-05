package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomMatrix;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRuleResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiHotelAvailabilityRequestDto {

  private List<String> hotelIds;
  private String roomStayStartDate;
  private String roomStayEndDate;
  private Integer roomStayQuantity;
  private List<String> roomTypes;
  private List<Integer> adults;
  private List<Integer> children;
  private List<Boolean> cotsRequired;
  private String ratePlanSet;
  private List<String> ratePlanCodes;
  private String channel;
  private String subchannel;
  private String language;
  private String companyId;
  RoomMatrix roomMatrix;
  private List<RoomSubstitutionRuleResponseDto> roomSubstitutions;
}
