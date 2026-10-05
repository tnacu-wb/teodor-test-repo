package uk.co.whitbread.reservation.domain.model.payment.in.ccui;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessItemsCcui {

  private String purchaseOrderNumber;
  private String customReferenceNumber;
  private List<BusinessAllowanceCcui> businessAllowances;
  private String businessNotes;
}
