package uk.co.whitbread.basket.domain.model.basket.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketStatusResponse {

  @NotEmpty
  private String basketReference;
  @NotEmpty
  private BasketStatus basketStatus;
  @NotEmpty
  private String createdAt;
  private Boolean retryPayment;

  private BasketError basketError;
}
