package uk.co.whitbread.payments.domain.model.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasketReservation {
  private List<String> reservationIds;
  private String hotelId;
  private BigDecimal totalCost;
  private BigDecimal outstandingBalance;
  private String currencyCode;
  private Set<RateInfoSummary> rateInfoList;
  private BigDecimal totalAmount;
  private String currency;
  private String paymentMethod;
  private Integer folioView;
  private Collection<PaymentPolicy> hotelPaymentPolicies;
  private LocalDate departureDate;
}
