package uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out;

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

  private PriceDto totalCost;
}
