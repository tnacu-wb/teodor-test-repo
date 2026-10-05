package uk.co.whitbread.kiosk.domain.model.checkin.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenuesAndBalances {

  private TotalFixedCharge totalFixedCharge;
  private TotalPayment totalPayment;
  private RoomRevenue roomRevenue;
  private FoodAndBevRevenue foodAndBevRevenue;
  private OtherRevenue otherRevenue;
  private NonRevenue nonRevenue;
  private TotalRevenue totalRevenue;
  private Balance balance;
  private CompBalance compBalance;


}
