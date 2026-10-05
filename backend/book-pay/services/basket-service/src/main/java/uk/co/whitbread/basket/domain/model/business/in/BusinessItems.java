package uk.co.whitbread.basket.domain.model.business.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessItems {

  private String purchaseOrderNumber;
  private String customReferenceNumber;
  private List<BusinessAllowance> businessAllowances;
  private String businessNotes;
}
