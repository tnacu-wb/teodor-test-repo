package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DistributionRatePlanDto {

  private String ratePlanCode;

  private String classification;

  private List<DistributionRoomDto> rooms;

}
