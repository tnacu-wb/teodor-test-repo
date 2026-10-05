package uk.co.whitbread.reservation.domain.model.payment.in;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessAccount {

  private String purchaseOrder;
  private String customerReference;
  private String cardNotPresentAuth;
  private Integer breakfastCodeReq;
  private BigDecimal dinnerAllowance;
  private String alcoholAllowed;
  private String carParkingAllowed;
  private String wifiAllowed;
  private String otherChargesAllowed;
}
