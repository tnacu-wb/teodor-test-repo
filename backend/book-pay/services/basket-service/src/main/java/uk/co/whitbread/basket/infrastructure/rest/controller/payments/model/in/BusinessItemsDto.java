package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class BusinessItemsDto implements SelfValidation<BusinessItemsDto> {

  private String purchaseOrderNumber;
  private String customReferenceNumber;
  private List<BusinessAllowanceDto> businessAllowances;
  private String businessNotes;

  public BusinessItemsDto(String purchaseOrderNumber, String customReferenceNumber,
      List<BusinessAllowanceDto> businessAllowances, String businessNotes) {
    this.purchaseOrderNumber = purchaseOrderNumber;
    this.customReferenceNumber = customReferenceNumber;
    this.businessAllowances = businessAllowances;
    this.businessNotes = businessNotes;
    this.validateSelf();
  }

}
