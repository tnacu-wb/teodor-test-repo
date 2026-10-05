package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperaRatePlanDto {

  private String code;

  private String name;

  private String description;

  private String classification;

  private String order;

  private List<List<OperaRoomDto>> rooms;

}
