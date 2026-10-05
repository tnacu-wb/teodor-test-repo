package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessItemsDto {

  private String purchaseOrderNumber;
  private String customReferenceNumber;
  private List<BusinessAllowanceDto> businessAllowances;
  private String businessNotes;
}
