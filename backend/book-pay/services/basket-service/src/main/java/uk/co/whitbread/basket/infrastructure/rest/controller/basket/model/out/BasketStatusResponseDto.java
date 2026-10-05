package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketStatusResponseDto {

  @NotEmpty
  private String basketReference;
  @NotEmpty
  private BasketStatusDto basketStatus;
  @NotEmpty
  private String createdAt;
  private Boolean retryPayment;

  private BasketErrorDto basketError;
}
