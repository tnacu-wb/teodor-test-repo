package uk.co.whitbread.basket.domain.model.basket.in;

import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrepaidDeposit {
  private String reservationId;
  private Long paymentNo;
  private List<Charge> charges;
  private Long cleanUpTime;

  @Override
  public boolean equals(Object newDeposit) {
    boolean result = false;

    if (newDeposit instanceof PrepaidDeposit castedNewDeposit) {
      if (this.reservationId.equals(castedNewDeposit.getReservationId())
          && this.getCharges().size() == castedNewDeposit.getCharges().size()) {
        return castedNewDeposit.getCharges().stream().filter(charge -> !this.getCharges().contains(charge)).toList()
            .isEmpty();
      }

    }

    return result;
  }

  @Override
  public int hashCode() {
    return this.reservationId.hashCode() + this.getCharges().size();
  }

}
