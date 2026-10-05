package uk.co.whitbread.cdh.adapter.service.generated.models.companyReports;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.companyReports.CompanyAnswersDto;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:17.912171+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EmployeeAnswersDto {

  @Valid
  private List<@Valid CompanyAnswersDto> companyAnswers = new ArrayList<>();

  private @Nullable String customerReferenceAnswer;

  private @Nullable String purchaseOrderAnswer;

  public EmployeeAnswersDto companyAnswers(List<@Valid CompanyAnswersDto> companyAnswers) {
    this.companyAnswers = companyAnswers;
    return this;
  }

  public EmployeeAnswersDto addCompanyAnswersItem(CompanyAnswersDto companyAnswersItem) {
    if (this.companyAnswers == null) {
      this.companyAnswers = new ArrayList<>();
    }
    this.companyAnswers.add(companyAnswersItem);
    return this;
  }

  /**
   * Get companyAnswers
   * @return companyAnswers
   */
  @Valid 
  @Schema(name = "companyAnswers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAnswers")
  public List<@Valid CompanyAnswersDto> getCompanyAnswers() {
    return companyAnswers;
  }

  public void setCompanyAnswers(List<@Valid CompanyAnswersDto> companyAnswers) {
    this.companyAnswers = companyAnswers;
  }

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmployeeAnswersDto employeeAnswersDto = (EmployeeAnswersDto) o;
    return Objects.equals(this.companyAnswers, employeeAnswersDto.companyAnswers) &&
        Objects.equals(this.customerReferenceAnswer, employeeAnswersDto.customerReferenceAnswer) &&
        Objects.equals(this.purchaseOrderAnswer, employeeAnswersDto.purchaseOrderAnswer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(companyAnswers, customerReferenceAnswer, purchaseOrderAnswer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmployeeAnswersDto {\n");
    sb.append("    companyAnswers: ").append(toIndentedString(companyAnswers)).append("\n");
    sb.append("    customerReferenceAnswer: ").append(toIndentedString(customerReferenceAnswer)).append("\n");
    sb.append("    purchaseOrderAnswer: ").append(toIndentedString(purchaseOrderAnswer)).append("\n");
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

