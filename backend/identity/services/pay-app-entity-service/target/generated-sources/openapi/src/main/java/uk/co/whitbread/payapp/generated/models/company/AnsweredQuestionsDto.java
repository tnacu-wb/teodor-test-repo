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
import uk.co.whitbread.payapp.generated.models.company.ManagementInformationQuestionAnsweredDto;
import uk.co.whitbread.payapp.generated.models.company.UserDefinedQuestionDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AnsweredQuestionsDto
 */

@JsonTypeName("AnsweredQuestions")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AnsweredQuestionsDto {

  private @Nullable ManagementInformationQuestionAnsweredDto customerReferenceManagement;

  private @Nullable ManagementInformationQuestionAnsweredDto purchaseOrderManagement;

  @Valid
  private List<@Valid UserDefinedQuestionDto> userDefinedQuestions = new ArrayList<>();

  public AnsweredQuestionsDto customerReferenceManagement(ManagementInformationQuestionAnsweredDto customerReferenceManagement) {
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
  public ManagementInformationQuestionAnsweredDto getCustomerReferenceManagement() {
    return customerReferenceManagement;
  }

  public void setCustomerReferenceManagement(ManagementInformationQuestionAnsweredDto customerReferenceManagement) {
    this.customerReferenceManagement = customerReferenceManagement;
  }

  public AnsweredQuestionsDto purchaseOrderManagement(ManagementInformationQuestionAnsweredDto purchaseOrderManagement) {
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
  public ManagementInformationQuestionAnsweredDto getPurchaseOrderManagement() {
    return purchaseOrderManagement;
  }

  public void setPurchaseOrderManagement(ManagementInformationQuestionAnsweredDto purchaseOrderManagement) {
    this.purchaseOrderManagement = purchaseOrderManagement;
  }

  public AnsweredQuestionsDto userDefinedQuestions(List<@Valid UserDefinedQuestionDto> userDefinedQuestions) {
    this.userDefinedQuestions = userDefinedQuestions;
    return this;
  }

  public AnsweredQuestionsDto addUserDefinedQuestionsItem(UserDefinedQuestionDto userDefinedQuestionsItem) {
    if (this.userDefinedQuestions == null) {
      this.userDefinedQuestions = new ArrayList<>();
    }
    this.userDefinedQuestions.add(userDefinedQuestionsItem);
    return this;
  }

  /**
   * Get userDefinedQuestions
   * @return userDefinedQuestions
   */
  @Valid 
  @Schema(name = "userDefinedQuestions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userDefinedQuestions")
  public List<@Valid UserDefinedQuestionDto> getUserDefinedQuestions() {
    return userDefinedQuestions;
  }

  public void setUserDefinedQuestions(List<@Valid UserDefinedQuestionDto> userDefinedQuestions) {
    this.userDefinedQuestions = userDefinedQuestions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnsweredQuestionsDto answeredQuestions = (AnsweredQuestionsDto) o;
    return Objects.equals(this.customerReferenceManagement, answeredQuestions.customerReferenceManagement) &&
        Objects.equals(this.purchaseOrderManagement, answeredQuestions.purchaseOrderManagement) &&
        Objects.equals(this.userDefinedQuestions, answeredQuestions.userDefinedQuestions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerReferenceManagement, purchaseOrderManagement, userDefinedQuestions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnsweredQuestionsDto {\n");
    sb.append("    customerReferenceManagement: ").append(toIndentedString(customerReferenceManagement)).append("\n");
    sb.append("    purchaseOrderManagement: ").append(toIndentedString(purchaseOrderManagement)).append("\n");
    sb.append("    userDefinedQuestions: ").append(toIndentedString(userDefinedQuestions)).append("\n");
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

