package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyManagementDetailsDto {
  private CustomerReferenceManagementDto customerReferenceManagement;
  private PurchaseOrderManagementDto purchaseOrderManagement;
  private List<QuestionsDto> questions;
  private PaymentDetailsDto paymentDetails;
  private List<RestrictedHotelCodeDto> restrictedHotelCodes;
  private List<RestrictedRatePlanDto> restrictedRatePlans;
}
