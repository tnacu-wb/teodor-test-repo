package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmendSummaryDetailsDto {

  private String charitable;
  private String previousTotal;
  private String balancePaid;
  private String payOnArrival;
  private String refund;
  private String nonRefundable;
  private String totalCost;
  private String balanceAuthorised;
  private PaymentOptionsDto paymentOptions;
  private NavigationOptionsDto navigationOptions;
  private PaymentCardDetailsDto paymentCardDetails;

}