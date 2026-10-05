package uk.co.whitbread.piba.registration.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredUserAccountOverview {
  private int primarySchemeCustomerId;
  private int schemeCustomerId;
  private String accountName;
  private String accountNumber;
}
