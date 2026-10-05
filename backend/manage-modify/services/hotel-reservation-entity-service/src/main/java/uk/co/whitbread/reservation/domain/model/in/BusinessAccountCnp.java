package uk.co.whitbread.reservation.domain.model.in;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAccountCnp {
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
