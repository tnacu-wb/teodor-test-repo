package uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BestRoomPrice {

  private String roomType;

  @Valid
  private Price price;

}
