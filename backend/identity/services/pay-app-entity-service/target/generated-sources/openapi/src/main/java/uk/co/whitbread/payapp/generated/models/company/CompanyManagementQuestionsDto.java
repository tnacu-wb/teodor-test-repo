package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 * CompanyManagementQuestionsDto
 */

@JsonTypeName("CompanyManagementQuestions")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyManagementQuestionsDto {

  private @Nullable ManagementInformationQuestionDto customerReferenceManagement;

  private @Nullable ManagementInformationQuestionDto purchaseOrderManagement;

  @Valid
  private List<@Valid ManagementInformationQuestionDto> userDefinedManagement = new ArrayList<>();

  public CompanyManagementQuestionsDto customerReferenceManagement(ManagementInformationQuestionDto customerReferenceManagement) {
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

  public CompanyManagementQuestionsDto purchaseOrderManagement(ManagementInformationQuestionDto purchaseOrderManagement) {
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

  public CompanyManagementQuestionsDto userDefinedManagement(List<@Valid ManagementInformationQuestionDto> userDefinedManagement) {
    this.userDefinedManagement = userDefinedManagement;
    return this;
  }

  public CompanyManagementQuestionsDto addUserDefinedManagementItem(ManagementInformationQuestionDto userDefinedManagementItem) {
    if (this.userDefinedManagement == null) {
      this.userDefinedManagement = new ArrayList<>();
    }
    this.userDefinedManagement.add(userDefinedManagementItem);
    return this;
  }

  /**
   * Get userDefinedManagement
   * @return userDefinedManagement
   */
  @Valid 
  @Schema(name = "userDefinedManagement", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userDefinedManagement")
  public List<@Valid ManagementInformationQuestionDto> getUserDefinedManagement() {
    return userDefinedManagement;
  }

  public void setUserDefinedManagement(List<@Valid ManagementInformationQuestionDto> userDefinedManagement) {
    this.userDefinedManagement = userDefinedManagement;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyManagementQuestionsDto companyManagementQuestions = (CompanyManagementQuestionsDto) o;
    return Objects.equals(this.customerReferenceManagement, companyManagementQuestions.customerReferenceManagement) &&
        Objects.equals(this.purchaseOrderManagement, companyManagementQuestions.purchaseOrderManagement) &&
        Objects.equals(this.userDefinedManagement, companyManagementQuestions.userDefinedManagement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerReferenceManagement, purchaseOrderManagement, userDefinedManagement);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyManagementQuestionsDto {\n");
    sb.append("    customerReferenceManagement: ").append(toIndentedString(customerReferenceManagement)).append("\n");
    sb.append("    purchaseOrderManagement: ").append(toIndentedString(purchaseOrderManagement)).append("\n");
    sb.append("    userDefinedManagement: ").append(toIndentedString(userDefinedManagement)).append("\n");
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

