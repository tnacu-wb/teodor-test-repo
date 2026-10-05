package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessAccountCnpDto {

  @Schema(example = "abc123")
  private String purchaseOrder;
  @Schema(example = "ab12")
  private String customerReference;
  @Schema(example = "Yes")
  private String cardNotPresentAuth;
  @Schema(example = "12")
  private Integer breakfastCodeReq;
  @Schema(example = "50")
  private BigDecimal dinnerAllowance;
  @Schema(example = "No")
  private String alcoholAllowed;
  @Schema(example = "Yes")
  private String carParkingAllowed;
  @Schema(example = "Yes")
  private String wifiAllowed;
  @Schema(example = "No")
  private String otherChargesAllowed;
}
