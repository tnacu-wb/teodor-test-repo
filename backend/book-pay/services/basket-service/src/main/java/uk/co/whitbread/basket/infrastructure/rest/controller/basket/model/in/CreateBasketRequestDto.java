package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class CreateBasketRequestDto implements SelfValidation<CreateBasketRequestDto> {

  @NotEmpty
  private String hotelId;
  private String userId;
  private String migratedResNo;
  private String originalBasketId;
  private Map<String, String> linkAmendReservations;
  private BasketStatus basketStatus;
  private PaymentOption paymentOption;
  private String paymentId;
  private String channel;
  private String subChannel;
  private String idContext;

  public CreateBasketRequestDto(String hotelId, String userId, String migratedResNo,
                                String originalBasketId, Map<String, String> linkAmendReservations,
                                BasketStatus basketStatus,
                                PaymentOption paymentOption, String paymentId, String channel, String subChannel,
                                String idContext) {
    this.hotelId = hotelId;
    this.userId = userId;
    this.migratedResNo = migratedResNo;
    this.originalBasketId = originalBasketId;
    this.linkAmendReservations = linkAmendReservations;
    this.basketStatus = basketStatus;
    this.paymentOption = paymentOption;
    this.paymentId = paymentId;
    this.channel = channel;
    this.subChannel = subChannel;
    this.idContext = idContext;
    this.validateSelf();
  }
}
