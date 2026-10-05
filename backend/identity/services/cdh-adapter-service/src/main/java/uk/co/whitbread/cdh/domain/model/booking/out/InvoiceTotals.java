package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTotals {

  @JsonProperty("BalanceAmount")
  private String balanceAmount;

  @JsonProperty("CreditAmount")
  private String creditAmount;

  @JsonProperty("DebitAmount")
  private String debitAmount;
}

