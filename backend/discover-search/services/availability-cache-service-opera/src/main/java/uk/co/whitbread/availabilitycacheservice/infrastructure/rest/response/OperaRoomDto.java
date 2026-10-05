package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperaRoomDto {

  private String type;

  private Boolean cotRequired;

  private Integer qtyRequested;

  private Integer quantityAvailable;

  private PriceDto totalCost;

  @Builder.Default
  private Boolean limitedAvailability = false;

}
