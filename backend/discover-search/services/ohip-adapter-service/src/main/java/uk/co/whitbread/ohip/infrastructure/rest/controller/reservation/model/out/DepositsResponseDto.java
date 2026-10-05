package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepositsResponseDto {

  public List<DepositsDto> deposits;

}