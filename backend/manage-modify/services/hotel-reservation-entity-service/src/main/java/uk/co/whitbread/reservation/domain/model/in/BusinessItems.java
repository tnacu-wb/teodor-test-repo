package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessItems {

  private String purchaseOrderNumber;

  private String customReferenceNumber;

  @NotEmpty
  private List<BusinessAllowance> businessAllowances;

  @NotEmpty
  private String businessNotes;

}
