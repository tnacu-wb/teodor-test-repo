package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class UpdateDiscountRequest implements SelfValidation<UpdateDiscountRequest> {

  @NotEmpty
  private Set<String> reservationIds;
  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String currency;
  @DecimalMin(value = "0.0")
  private BigDecimal discountAmount;

  public UpdateDiscountRequest(Set<String> reservationIds, String hotelId, String currency,
      BigDecimal discountAmount) {
    this.reservationIds = reservationIds;
    this.hotelId = hotelId;
    this.currency = currency;
    this.discountAmount = discountAmount;
    this.validateSelf();
  }
}
