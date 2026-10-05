package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatePlanOperaDto {

  private String code;

  private String classification;

  private String description;

  private String name;

  private String order;

  private List<List<RoomOperaDto>> rooms;

}
