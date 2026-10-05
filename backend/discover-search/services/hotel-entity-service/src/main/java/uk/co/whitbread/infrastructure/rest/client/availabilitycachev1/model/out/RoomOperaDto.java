package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomOperaDto {

  private String type;

  private Boolean cotRequired;

  private Integer qtyRequested;

  private Integer quantityAvailable;

  private TotalCostDto totalCost;

  public RoomOperaDto(RoomOperaDto source) {
    this.type = source.type;
    this.cotRequired = source.cotRequired;
    this.qtyRequested = source.qtyRequested;
    this.quantityAvailable = source.quantityAvailable;
    this.totalCost = source.totalCost;
  }

}
