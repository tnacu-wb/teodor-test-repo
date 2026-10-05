package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreAuthChargesDto {
  private String preAuthCharges;

}
