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
public class InvoiceVat {

  @JsonProperty("RateDesc")
  private String rateDesc;

  @JsonProperty("Rate")
  private String rate;

  @JsonProperty("TotalVAT")
  private String totalVat;

  @JsonProperty("TotalExclVAT")
  private String totalExclVat;

  @JsonProperty("TotalIncVAT")
  private String totalIncVat;
}

