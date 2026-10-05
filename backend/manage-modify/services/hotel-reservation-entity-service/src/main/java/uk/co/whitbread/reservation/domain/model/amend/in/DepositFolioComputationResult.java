package uk.co.whitbread.reservation.domain.model.amend.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositFolioComputationResult {

  private Boolean markAsPayOnArrival;
  private String paymentId;
  private String defaultPaymentMethod;
  private String currencyCode;
  private BigDecimal totalRefundAmt;
  private Map<String, Map<String, List<BigDecimal>>> depositFoliosAfterAmend;
}