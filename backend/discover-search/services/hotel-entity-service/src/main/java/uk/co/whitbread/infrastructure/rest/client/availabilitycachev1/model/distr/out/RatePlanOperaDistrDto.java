package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatePlanOperaDistrDto {

  private String ratePlanCode;

  private String classification;

  private String description;

  private String name;

  private String order;

  private List<RoomOperaDistrDto> rooms;

}
