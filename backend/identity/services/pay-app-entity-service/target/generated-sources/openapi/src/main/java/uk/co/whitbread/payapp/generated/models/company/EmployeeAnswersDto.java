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
import uk.co.whitbread.payapp.generated.models.company.UserDefinedAnswerDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EmployeeAnswersDto
 */

@JsonTypeName("EmployeeAnswers")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EmployeeAnswersDto {

  private @Nullable String customerReferenceAnswer;

  private @Nullable String purchaseOrderAnswer;

  @Valid
  private List<@Valid UserDefinedAnswerDto> userDefinedAnswers = new ArrayList<>();

  public EmployeeAnswersDto customerReferenceAnswer(String customerReferenceAnswer) {
    this.customerReferenceAnswer = customerReferenceAnswer;
    return this;
  }

  /**
   * Get customerReferenceAnswer
   * @return customerReferenceAnswer
   */
  
  @Schema(name = "customerReferenceAnswer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReferenceAnswer")
  public String getCustomerReferenceAnswer() {
    return customerReferenceAnswer;
  }

  public void setCustomerReferenceAnswer(String customerReferenceAnswer) {
    this.customerReferenceAnswer = customerReferenceAnswer;
  }

  public EmployeeAnswersDto purchaseOrderAnswer(String purchaseOrderAnswer) {
    this.purchaseOrderAnswer = purchaseOrderAnswer;
    return this;
  }

  /**
   * Get purchaseOrderAnswer
   * @return purchaseOrderAnswer
   */
  
  @Schema(name = "purchaseOrderAnswer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrderAnswer")
  public String getPurchaseOrderAnswer() {
    return purchaseOrderAnswer;
  }

  public void setPurchaseOrderAnswer(String purchaseOrderAnswer) {
    this.purchaseOrderAnswer = purchaseOrderAnswer;
  }

  public EmployeeAnswersDto userDefinedAnswers(List<@Valid UserDefinedAnswerDto> userDefinedAnswers) {
    this.userDefinedAnswers = userDefinedAnswers;
    return this;
  }

  public EmployeeAnswersDto addUserDefinedAnswersItem(UserDefinedAnswerDto userDefinedAnswersItem) {
    if (this.userDefinedAnswers == null) {
      this.userDefinedAnswers = new ArrayList<>();
    }
    this.userDefinedAnswers.add(userDefinedAnswersItem);
    return this;
  }

  /**
   * Get userDefinedAnswers
   * @return userDefinedAnswers
   */
  @Valid 
  @Schema(name = "userDefinedAnswers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userDefinedAnswers")
  public List<@Valid UserDefinedAnswerDto> getUserDefinedAnswers() {
    return userDefinedAnswers;
  }

  public void setUserDefinedAnswers(List<@Valid UserDefinedAnswerDto> userDefinedAnswers) {
    this.userDefinedAnswers = userDefinedAnswers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmployeeAnswersDto employeeAnswers = (EmployeeAnswersDto) o;
    return Objects.equals(this.customerReferenceAnswer, employeeAnswers.customerReferenceAnswer) &&
        Objects.equals(this.purchaseOrderAnswer, employeeAnswers.purchaseOrderAnswer) &&
        Objects.equals(this.userDefinedAnswers, employeeAnswers.userDefinedAnswers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerReferenceAnswer, purchaseOrderAnswer, userDefinedAnswers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmployeeAnswersDto {\n");
    sb.append("    customerReferenceAnswer: ").append(toIndentedString(customerReferenceAnswer)).append("\n");
    sb.append("    purchaseOrderAnswer: ").append(toIndentedString(purchaseOrderAnswer)).append("\n");
    sb.append("    userDefinedAnswers: ").append(toIndentedString(userDefinedAnswers)).append("\n");
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

