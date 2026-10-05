package uk.co.whitbread.payments.domain.model.out;

import java.time.LocalDate;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
  private String hotelId;
  private Collection<PaymentPolicy> hotelPaymentPolicies;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private String ratePlanCode;
  private String channel;
}
