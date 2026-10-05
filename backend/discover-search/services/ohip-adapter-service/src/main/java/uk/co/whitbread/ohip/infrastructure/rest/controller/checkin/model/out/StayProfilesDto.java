package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StayProfilesDto {

  private List<CheckInProfileIdListDto> profileIdList;
  private CheckInProfileDetailsDto profile;
  private String reservationProfileType;


}
