package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CurrencyAmountTypeDto {

  private BigDecimal amount;
  private String currencyCode;
}
