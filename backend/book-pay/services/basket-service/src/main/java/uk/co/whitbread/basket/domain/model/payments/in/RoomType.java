package uk.co.whitbread.basket.domain.model.payments.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class RoomType implements SelfValidation<RoomType> {

  private String type;
  private String rate;
  private Integer adultsNumber;

  public RoomType(String type, String rate, Integer adultsNumber) {
    this.type = type;
    this.rate = rate;
    this.adultsNumber = adultsNumber;
    this.validateSelf();
  }
}
