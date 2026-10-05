package uk.co.whitbread.piba.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
  @JsonProperty("billingFrequency")
  private String billingFrequency;

  @JsonProperty("daysToPay")
  private Integer daysToPay;

  @JsonProperty("status")
  private String status;

  @JsonProperty("statementValue")
  private AccountValue statementValue;

  @JsonProperty("outStandingBalance")
  private AccountValue outStandingBalance;
}
