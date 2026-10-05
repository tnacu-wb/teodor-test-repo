package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoResponseDto {
  private String billingFrequency;
  private Integer daysToPay;
  private String status;
  private AccountValueResponseDto statementValue;
  private AccountValueResponseDto outStandingBalance;

}
