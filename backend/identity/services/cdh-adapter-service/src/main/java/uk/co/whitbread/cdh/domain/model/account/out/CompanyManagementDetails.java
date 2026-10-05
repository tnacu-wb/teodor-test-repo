package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyManagementDetails {
  @JsonProperty("CustomerReferenceManagement")
  private CustomerReferenceManagement customerReferenceManagement;
  @JsonProperty("PurchaseOrderManagement")
  private PurchaseOrderManagement purchaseOrderManagement;
  @JsonProperty("Questions")
  private List<Questions> questions;
  @JsonProperty("PaymentDetails")
  private PaymentDetails paymentDetails;
  @JsonProperty("RestrictedHotelCodes")
  private List<RestrictedHotelCode> restrictedHotelCodes;
  @JsonProperty("RestrictedRatePlans")
  private List<RestrictedRatePlan> restrictedRatePlans;
}
