package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class BusinessItems implements SelfValidation<BusinessItems> {

  private String purchaseOrderNumber;
  private String customReferenceNumber;
  @NotEmpty
  private List<BusinessAllowance> businessAllowances;

  @NotEmpty
  private String businessNotes;

  public BusinessItems(
      String purchaseOrderNumber,
      String customReferenceNumber,
      List<BusinessAllowance> businessAllowances,
      String businessNotes) {
    this.purchaseOrderNumber = purchaseOrderNumber;
    this.customReferenceNumber = customReferenceNumber;
    this.businessAllowances = businessAllowances;
    this.businessNotes = businessNotes;
    this.validateSelf();
  }
}
