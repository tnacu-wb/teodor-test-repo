package uk.co.whitbread.basket.domain.model.basket.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class CreateBasketRequest implements SelfValidation<CreateBasketRequest> {

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
  private List<BasketItem> basketItems;
  private List<AddBasketItemType> basketItemTypes;


  public CreateBasketRequest(String hotelId, String userId, String migratedResNo,
                             String originalBasketId, Map<String, String> linkAmendReservations,
                             BasketStatus basketStatus,
                             PaymentOption paymentOption, String paymentId, String channel, String subChannel,
                             String idContext, List<BasketItem> basketItems, List<AddBasketItemType> basketItemTypes) {
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
    this.basketItems = basketItems;
    this.basketItemTypes = basketItemTypes;
    this.validateSelf();
  }
}
