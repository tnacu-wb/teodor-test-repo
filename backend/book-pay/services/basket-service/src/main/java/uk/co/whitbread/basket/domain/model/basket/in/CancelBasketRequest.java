package uk.co.whitbread.basket.domain.model.basket.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.reservation.out.Deposits;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class CancelBasketRequest implements SelfValidation<CreateBasketRequest> {
  private String basketReference;
  private Boolean isFailed;
  private Boolean sendEmail;
  private List<Deposits> deposits;

  public CancelBasketRequest(String basketReference, Boolean isFailed, Boolean sendMail, List<Deposits> deposits) {
    this.basketReference = basketReference;
    this.isFailed = isFailed;
    this.sendEmail = sendMail;
    this.deposits = deposits;
    this.validateSelf();
  }
}
