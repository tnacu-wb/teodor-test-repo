package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatePlanDto {

  private String code;

  private String name;

  private String description;

  private String classification;

  private String order;

  private PriceDto totalCost;

  private List<RoomDto> rooms;

}
