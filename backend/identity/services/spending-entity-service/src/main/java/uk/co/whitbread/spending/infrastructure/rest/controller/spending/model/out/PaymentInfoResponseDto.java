package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentInfoResponseDto {

  private List<PaymentDto> payments;

  private String errors;
}
