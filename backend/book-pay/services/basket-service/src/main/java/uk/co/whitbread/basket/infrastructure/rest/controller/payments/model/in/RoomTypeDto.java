package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class RoomTypeDto implements SelfValidation<RoomTypeDto> {

  private String type;
  private String rate;
  private Integer adultsNumber;

  public RoomTypeDto(String type, String rate, Integer adultsNumber) {
    this.type = type;
    this.rate = rate;
    this.adultsNumber = adultsNumber;
    this.validateSelf();
  }
}
