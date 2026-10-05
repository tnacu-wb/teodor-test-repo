package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CustomerReferenceManagementDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.PaymentDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.PurchaseOrderManagementDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.QuestionsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.RestrictedHotelCodeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.RestrictedRatePlanDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CompanyManagementDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyManagementDetailsDto {

  private @Nullable CustomerReferenceManagementDto customerReferenceManagement;

  private @Nullable PaymentDetailsDto paymentDetails;

  private @Nullable PurchaseOrderManagementDto purchaseOrderManagement;

  @Valid
  private List<@Valid QuestionsDto> questions = new ArrayList<>();

  @Valid
  private List<@Valid RestrictedHotelCodeDto> restrictedHotelCodes = new ArrayList<>();

  @Valid
  private List<@Valid RestrictedRatePlanDto> restrictedRatePlans = new ArrayList<>();

  public CompanyManagementDetailsDto customerReferenceManagement(CustomerReferenceManagementDto customerReferenceManagement) {
    this.customerReferenceManagement = customerReferenceManagement;
    return this;
  }

  /**
   * Get customerReferenceManagement
   * @return customerReferenceManagement
   */
  @Valid 
  @Schema(name = "customerReferenceManagement", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReferenceManagement")
  public CustomerReferenceManagementDto getCustomerReferenceManagement() {
    return customerReferenceManagement;
  }

  public void setCustomerReferenceManagement(CustomerReferenceManagementDto customerReferenceManagement) {
    this.customerReferenceManagement = customerReferenceManagement;
  }

  public CompanyManagementDetailsDto paymentDetails(PaymentDetailsDto paymentDetails) {
    this.paymentDetails = paymentDetails;
    return this;
  }

  /**
   * Get paymentDetails
   * @return paymentDetails
   */
  @Valid 
  @Schema(name = "paymentDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentDetails")
  public PaymentDetailsDto getPaymentDetails() {
    return paymentDetails;
  }

  public void setPaymentDetails(PaymentDetailsDto paymentDetails) {
    this.paymentDetails = paymentDetails;
  }

  public CompanyManagementDetailsDto purchaseOrderManagement(PurchaseOrderManagementDto purchaseOrderManagement) {
    this.purchaseOrderManagement = purchaseOrderManagement;
    return this;
  }

  /**
   * Get purchaseOrderManagement
   * @return purchaseOrderManagement
   */
  @Valid 
  @Schema(name = "purchaseOrderManagement", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrderManagement")
  public PurchaseOrderManagementDto getPurchaseOrderManagement() {
    return purchaseOrderManagement;
  }

  public void setPurchaseOrderManagement(PurchaseOrderManagementDto purchaseOrderManagement) {
    this.purchaseOrderManagement = purchaseOrderManagement;
  }

  public CompanyManagementDetailsDto questions(List<@Valid QuestionsDto> questions) {
    this.questions = questions;
    return this;
  }

  public CompanyManagementDetailsDto addQuestionsItem(QuestionsDto questionsItem) {
    if (this.questions == null) {
      this.questions = new ArrayList<>();
    }
    this.questions.add(questionsItem);
    return this;
  }

  /**
   * Get questions
   * @return questions
   */
  @Valid 
  @Schema(name = "questions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("questions")
  public List<@Valid QuestionsDto> getQuestions() {
    return questions;
  }

  public void setQuestions(List<@Valid QuestionsDto> questions) {
    this.questions = questions;
  }

  public CompanyManagementDetailsDto restrictedHotelCodes(List<@Valid RestrictedHotelCodeDto> restrictedHotelCodes) {
    this.restrictedHotelCodes = restrictedHotelCodes;
    return this;
  }

  public CompanyManagementDetailsDto addRestrictedHotelCodesItem(RestrictedHotelCodeDto restrictedHotelCodesItem) {
    if (this.restrictedHotelCodes == null) {
      this.restrictedHotelCodes = new ArrayList<>();
    }
    this.restrictedHotelCodes.add(restrictedHotelCodesItem);
    return this;
  }

  /**
   * Get restrictedHotelCodes
   * @return restrictedHotelCodes
   */
  @Valid 
  @Schema(name = "restrictedHotelCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictedHotelCodes")
  public List<@Valid RestrictedHotelCodeDto> getRestrictedHotelCodes() {
    return restrictedHotelCodes;
  }

  public void setRestrictedHotelCodes(List<@Valid RestrictedHotelCodeDto> restrictedHotelCodes) {
    this.restrictedHotelCodes = restrictedHotelCodes;
  }

  public CompanyManagementDetailsDto restrictedRatePlans(List<@Valid RestrictedRatePlanDto> restrictedRatePlans) {
    this.restrictedRatePlans = restrictedRatePlans;
    return this;
  }

  public CompanyManagementDetailsDto addRestrictedRatePlansItem(RestrictedRatePlanDto restrictedRatePlansItem) {
    if (this.restrictedRatePlans == null) {
      this.restrictedRatePlans = new ArrayList<>();
    }
    this.restrictedRatePlans.add(restrictedRatePlansItem);
    return this;
  }

  /**
   * Get restrictedRatePlans
   * @return restrictedRatePlans
   */
  @Valid 
  @Schema(name = "restrictedRatePlans", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictedRatePlans")
  public List<@Valid RestrictedRatePlanDto> getRestrictedRatePlans() {
    return restrictedRatePlans;
  }

  public void setRestrictedRatePlans(List<@Valid RestrictedRatePlanDto> restrictedRatePlans) {
    this.restrictedRatePlans = restrictedRatePlans;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyManagementDetailsDto companyManagementDetailsDto = (CompanyManagementDetailsDto) o;
    return Objects.equals(this.customerReferenceManagement, companyManagementDetailsDto.customerReferenceManagement) &&
        Objects.equals(this.paymentDetails, companyManagementDetailsDto.paymentDetails) &&
        Objects.equals(this.purchaseOrderManagement, companyManagementDetailsDto.purchaseOrderManagement) &&
        Objects.equals(this.questions, companyManagementDetailsDto.questions) &&
        Objects.equals(this.restrictedHotelCodes, companyManagementDetailsDto.restrictedHotelCodes) &&
        Objects.equals(this.restrictedRatePlans, companyManagementDetailsDto.restrictedRatePlans);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerReferenceManagement, paymentDetails, purchaseOrderManagement, questions, restrictedHotelCodes, restrictedRatePlans);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyManagementDetailsDto {\n");
    sb.append("    customerReferenceManagement: ").append(toIndentedString(customerReferenceManagement)).append("\n");
    sb.append("    paymentDetails: ").append(toIndentedString(paymentDetails)).append("\n");
    sb.append("    purchaseOrderManagement: ").append(toIndentedString(purchaseOrderManagement)).append("\n");
    sb.append("    questions: ").append(toIndentedString(questions)).append("\n");
    sb.append("    restrictedHotelCodes: ").append(toIndentedString(restrictedHotelCodes)).append("\n");
    sb.append("    restrictedRatePlans: ").append(toIndentedString(restrictedRatePlans)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

