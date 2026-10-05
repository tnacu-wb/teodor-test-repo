package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class DepositsDto {
  private String paymentReference;
  private CurrencyAmountTypeDto postedAmount;
}

