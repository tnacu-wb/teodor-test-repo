package uk.co.whitbread.shared.cdh.model.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyManagementDetails {

  @JsonProperty("CustomerReferenceManagement")
  private CompanyQuestion customerReferenceManagement;
  @JsonProperty("PurchaseOrderManagement")
  private CompanyQuestion purchaseOrderManagement;
  @JsonProperty("Questions")
  private List<GetQuestionResponse> questions;
  @JsonProperty("PaymentDetails")
  private PaymentDetails paymentDetails;
  @JsonProperty("RestrictedHotelCodes")
  private List<CompanyCode> restrictedHotelCodes;
  @JsonProperty("RestrictedRatePlans")
  private List<CompanyCode> restrictedRatePlans;
}
