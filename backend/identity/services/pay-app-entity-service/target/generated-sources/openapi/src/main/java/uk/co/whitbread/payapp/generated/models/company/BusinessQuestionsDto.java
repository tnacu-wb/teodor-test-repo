package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.ManagementInformationQuestionDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BusinessQuestionsDto
 */

@JsonTypeName("BusinessQuestions")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessQuestionsDto {

  private @Nullable ManagementInformationQuestionDto customerReferenceManagement;

  private @Nullable ManagementInformationQuestionDto purchaseOrderManagement;

  public BusinessQuestionsDto customerReferenceManagement(ManagementInformationQuestionDto customerReferenceManagement) {
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
  public ManagementInformationQuestionDto getCustomerReferenceManagement() {
    return customerReferenceManagement;
  }

  public void setCustomerReferenceManagement(ManagementInformationQuestionDto customerReferenceManagement) {
    this.customerReferenceManagement = customerReferenceManagement;
  }

  public BusinessQuestionsDto purchaseOrderManagement(ManagementInformationQuestionDto purchaseOrderManagement) {
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
  public ManagementInformationQuestionDto getPurchaseOrderManagement() {
    return purchaseOrderManagement;
  }

  public void setPurchaseOrderManagement(ManagementInformationQuestionDto purchaseOrderManagement) {
    this.purchaseOrderManagement = purchaseOrderManagement;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessQuestionsDto businessQuestions = (BusinessQuestionsDto) o;
    return Objects.equals(this.customerReferenceManagement, businessQuestions.customerReferenceManagement) &&
        Objects.equals(this.purchaseOrderManagement, businessQuestions.purchaseOrderManagement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerReferenceManagement, purchaseOrderManagement);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessQuestionsDto {\n");
    sb.append("    customerReferenceManagement: ").append(toIndentedString(customerReferenceManagement)).append("\n");
    sb.append("    purchaseOrderManagement: ").append(toIndentedString(purchaseOrderManagement)).append("\n");
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

