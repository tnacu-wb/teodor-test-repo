package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class DepositFolioRequest implements SelfValidation<DepositFolioRequest> {

  @NotEmpty
  private String reservationId;
  @NotEmpty
  private String hotelId;
  @NotNull
  private PaymentOption paymentOption;
  private String paymentMethod;
  private String paymentType;

  private PaymentCard paymentCard;

  private boolean isCancelRequest;

  private BigDecimal totalCostOfStay;

  private String paymentId;

  public DepositFolioRequest(String reservationId, String hotelId, PaymentOption paymentOption,
      String paymentMethod, String paymentType, PaymentCard paymentCard,
      boolean isCancelRequest, BigDecimal totalCostOfStay, String paymentId) {
    this.reservationId = reservationId;
    this.hotelId = hotelId;
    this.paymentCard = paymentCard;
    this.paymentOption = paymentOption;
    this.isCancelRequest = isCancelRequest;
    this.totalCostOfStay = totalCostOfStay;
    this.paymentMethod = paymentMethod;
    this.paymentType = paymentType;
    this.paymentId = paymentId;
    this.validateSelf();
  }
}