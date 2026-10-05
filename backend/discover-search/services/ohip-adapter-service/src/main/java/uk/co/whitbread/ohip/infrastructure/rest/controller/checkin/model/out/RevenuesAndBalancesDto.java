package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenuesAndBalancesDto {

  private TotalFixedChargeDto totalFixedCharge;
  private TotalPaymentDto totalPayment;
  private RoomRevenueDto roomRevenue;
  private FoodAndBevRevenueDto foodAndBevRevenue;
  private OtherRevenueDto otherRevenue;
  private NonRevenueDto nonRevenue;
  private TotalRevenueDto totalRevenue;
  private BalanceDto balance;
  private CompBalanceDto compBalance;


}
