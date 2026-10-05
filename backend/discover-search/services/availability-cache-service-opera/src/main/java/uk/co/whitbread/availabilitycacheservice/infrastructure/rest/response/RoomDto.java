package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

  private String type;

  private Integer adults;

  private Integer children;

  private Boolean cotRequired;

  private PriceDto totalCost;
}
