package uk.co.whitbread.spending.domain.model.out.pibaaccountservice;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAccountsResponse {

  List<CustomerAccount> accounts;
  int totalRecordCount;
}