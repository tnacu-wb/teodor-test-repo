package uk.co.whitbread.basket.domain.model.basket.in;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Charge implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String transactionCode;
  private Integer postingQuantity;
  private String postingReference;
  private ChargeAmount chargeAmount;

  @Override
  public boolean equals(Object newCharge) {
    boolean result = false;

    if (newCharge instanceof Charge castedCharge) {
      result = this.transactionCode.equals(castedCharge.getTransactionCode())
          && Objects.equals(this.postingQuantity, castedCharge.getPostingQuantity())
          && this.postingReference.equals(castedCharge.getPostingReference())
          && this.getChargeAmount().getAmount().compareTo(castedCharge.getChargeAmount().getAmount()) == 0;
    }

    return result;
  }

  @Override
  public int hashCode() {
    return this.transactionCode.hashCode() + this.postingQuantity + this.getChargeAmount().getAmount().hashCode();
  }

}
