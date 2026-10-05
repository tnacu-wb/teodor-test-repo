package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EckohBillingDto {

  @JsonSetter(nulls = Nulls.SKIP)
  private EckohAddressDto address = new EckohAddressDto("N/A", "N/A", "N/A", "N/A", "N/A", "N/A");
}
