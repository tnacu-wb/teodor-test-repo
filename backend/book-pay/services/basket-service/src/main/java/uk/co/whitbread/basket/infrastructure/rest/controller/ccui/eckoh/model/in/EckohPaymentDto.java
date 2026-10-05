package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EckohPaymentDto {

  @NotEmpty
  private String type;
  @JsonSetter(nulls = Nulls.SKIP)
  private String subType = "ECKOH";
  @JsonSetter(nulls = Nulls.SKIP)
  private String environment = "N/A";
  @JsonSetter(nulls = Nulls.SKIP)
  private EckohBillingDto billing = new EckohBillingDto();
  @JsonSetter(nulls = Nulls.SKIP)
  private EckohAmountDto amount = new EckohAmountDto("GBP", 0);
}
