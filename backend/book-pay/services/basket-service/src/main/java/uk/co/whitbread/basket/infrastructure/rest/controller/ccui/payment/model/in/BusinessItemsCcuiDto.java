package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessItemsCcuiDto {

  private String purchaseOrderNumber;
  private String customReferenceNumber;
  private List<BusinessAllowanceCcuiDto> businessAllowances;
  private String businessNotes;
}
