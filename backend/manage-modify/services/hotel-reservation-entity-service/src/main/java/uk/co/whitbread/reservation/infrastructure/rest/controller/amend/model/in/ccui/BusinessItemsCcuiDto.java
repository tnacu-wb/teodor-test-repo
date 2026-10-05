package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ccui;

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
