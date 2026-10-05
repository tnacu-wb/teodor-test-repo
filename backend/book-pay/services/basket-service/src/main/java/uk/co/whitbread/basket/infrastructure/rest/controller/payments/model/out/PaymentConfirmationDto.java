package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmationDto {
  private String reference;
  private URI basketUri;
}
