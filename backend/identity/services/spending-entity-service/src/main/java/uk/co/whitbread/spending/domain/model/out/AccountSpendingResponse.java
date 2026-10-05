package uk.co.whitbread.spending.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.spending.domain.model.out.cdh.AccountSpending;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSpendingResponse {

  private List<AccountSpending> accountSpendingList;

}
