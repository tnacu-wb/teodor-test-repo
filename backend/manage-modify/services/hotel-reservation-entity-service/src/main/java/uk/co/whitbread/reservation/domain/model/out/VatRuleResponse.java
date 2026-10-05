package uk.co.whitbread.reservation.domain.model.out;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VatRuleResponse {

  private Date generatedAt;
  private List<TransactionCode> tranCodes;
  private String vatRegion;

}
