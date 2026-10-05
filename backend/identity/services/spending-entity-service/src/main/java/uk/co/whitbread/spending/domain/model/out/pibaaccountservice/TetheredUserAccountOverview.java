package uk.co.whitbread.spending.domain.model.out.pibaaccountservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TetheredUserAccountOverview {
  protected int primarySchemeCustomerId;
  protected int schemeCustomerId;
  protected String accountName;
  protected String accountNumber;
}
