package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.AllowanceValue;

@Data
@Builder
@NoArgsConstructor
@AllowanceValue
public class BusinessAccountDto implements SelfValidation<BusinessAccountDto> {

  private String purchaseOrder;
  private String customerReference;
  private String cardNotPresentAuth;
  private Integer breakfastCodeReq;
  private BigDecimal dinnerAllowance;
  private String alcoholAllowed;
  private String carParkingAllowed;
  private String wifiAllowed;
  private String otherChargesAllowed;

  public BusinessAccountDto(String purchaseOrder, String customerReference,
                            String cardNotPresentAuth, Integer breakfastCodeReq,
                            BigDecimal dinnerAllowance, String alcoholAllowed,
                            String carParkingAllowed, String wifiAllowed,
                            String otherChargesAllowed) {
    this.purchaseOrder = purchaseOrder;
    this.customerReference = customerReference;
    this.cardNotPresentAuth = cardNotPresentAuth;
    this.breakfastCodeReq = breakfastCodeReq;
    this.dinnerAllowance = dinnerAllowance;
    this.alcoholAllowed = alcoholAllowed;
    this.carParkingAllowed = carParkingAllowed;
    this.wifiAllowed = wifiAllowed;
    this.otherChargesAllowed = otherChargesAllowed;
    this.validateSelf();
  }
}

