package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonguaranteedItemsDto {

  private String typeOfCaller;
}
