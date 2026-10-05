package uk.co.whitbread.shared.cdh.model.spending.transaction;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDetailsResponse {

  @JsonProperty("TotalBookingValue")
  private BigDecimal totalBookingValue;

  @JsonProperty("Transactions")
  private List<Transaction> transactions;

  @JsonProperty("Paging")
  private Paging paging;

}
