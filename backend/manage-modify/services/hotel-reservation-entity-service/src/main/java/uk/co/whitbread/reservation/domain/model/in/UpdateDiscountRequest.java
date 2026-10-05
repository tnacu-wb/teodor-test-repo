package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDiscountRequest {
  @DecimalMin(value = "0.0")
  private BigDecimal discountAmount;
  @NotEmpty
  private String currency;
  @NotEmpty
  private String hotelId;
  @NotEmpty
  private List<String> reservationIds;
}
