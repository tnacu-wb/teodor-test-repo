package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CompanyQuestionAndAnswerDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CompanyQuestionAndAnswerDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyQuestionAndAnswerDetailsDto {

  private @Nullable CompanyQuestionAndAnswerDto customerReferenceQuestionAndAnswer;

  private @Nullable CompanyQuestionAndAnswerDto purchaseOrderQuestionAndAnswer;

  @Valid
  private List<@Valid CompanyQuestionAndAnswerDto> userDefinedQuestionAndAnswers = new ArrayList<>();

  public CompanyQuestionAndAnswerDetailsDto customerReferenceQuestionAndAnswer(CompanyQuestionAndAnswerDto customerReferenceQuestionAndAnswer) {
    this.customerReferenceQuestionAndAnswer = customerReferenceQuestionAndAnswer;
    return this;
  }

  /**
   * Get customerReferenceQuestionAndAnswer
   * @return customerReferenceQuestionAndAnswer
   */
  @Valid 
  @Schema(name = "customerReferenceQuestionAndAnswer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReferenceQuestionAndAnswer")
  public CompanyQuestionAndAnswerDto getCustomerReferenceQuestionAndAnswer() {
    return customerReferenceQuestionAndAnswer;
  }

  public void setCustomerReferenceQuestionAndAnswer(CompanyQuestionAndAnswerDto customerReferenceQuestionAndAnswer) {
    this.customerReferenceQuestionAndAnswer = customerReferenceQuestionAndAnswer;
  }

  public CompanyQuestionAndAnswerDetailsDto purchaseOrderQuestionAndAnswer(CompanyQuestionAndAnswerDto purchaseOrderQuestionAndAnswer) {
    this.purchaseOrderQuestionAndAnswer = purchaseOrderQuestionAndAnswer;
    return this;
  }

  /**
   * Get purchaseOrderQuestionAndAnswer
   * @return purchaseOrderQuestionAndAnswer
   */
  @Valid 
  @Schema(name = "purchaseOrderQuestionAndAnswer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrderQuestionAndAnswer")
  public CompanyQuestionAndAnswerDto getPurchaseOrderQuestionAndAnswer() {
    return purchaseOrderQuestionAndAnswer;
  }

  public void setPurchaseOrderQuestionAndAnswer(CompanyQuestionAndAnswerDto purchaseOrderQuestionAndAnswer) {
    this.purchaseOrderQuestionAndAnswer = purchaseOrderQuestionAndAnswer;
  }

  public CompanyQuestionAndAnswerDetailsDto userDefinedQuestionAndAnswers(List<@Valid CompanyQuestionAndAnswerDto> userDefinedQuestionAndAnswers) {
    this.userDefinedQuestionAndAnswers = userDefinedQuestionAndAnswers;
    return this;
  }

  public CompanyQuestionAndAnswerDetailsDto addUserDefinedQuestionAndAnswersItem(CompanyQuestionAndAnswerDto userDefinedQuestionAndAnswersItem) {
    if (this.userDefinedQuestionAndAnswers == null) {
      this.userDefinedQuestionAndAnswers = new ArrayList<>();
    }
    this.userDefinedQuestionAndAnswers.add(userDefinedQuestionAndAnswersItem);
    return this;
  }

  /**
   * Get userDefinedQuestionAndAnswers
   * @return userDefinedQuestionAndAnswers
   */
  @Valid 
  @Schema(name = "userDefinedQuestionAndAnswers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userDefinedQuestionAndAnswers")
  public List<@Valid CompanyQuestionAndAnswerDto> getUserDefinedQuestionAndAnswers() {
    return userDefinedQuestionAndAnswers;
  }

  public void setUserDefinedQuestionAndAnswers(List<@Valid CompanyQuestionAndAnswerDto> userDefinedQuestionAndAnswers) {
    this.userDefinedQuestionAndAnswers = userDefinedQuestionAndAnswers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetailsDto = (CompanyQuestionAndAnswerDetailsDto) o;
    return Objects.equals(this.customerReferenceQuestionAndAnswer, companyQuestionAndAnswerDetailsDto.customerReferenceQuestionAndAnswer) &&
        Objects.equals(this.purchaseOrderQuestionAndAnswer, companyQuestionAndAnswerDetailsDto.purchaseOrderQuestionAndAnswer) &&
        Objects.equals(this.userDefinedQuestionAndAnswers, companyQuestionAndAnswerDetailsDto.userDefinedQuestionAndAnswers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerReferenceQuestionAndAnswer, purchaseOrderQuestionAndAnswer, userDefinedQuestionAndAnswers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyQuestionAndAnswerDetailsDto {\n");
    sb.append("    customerReferenceQuestionAndAnswer: ").append(toIndentedString(customerReferenceQuestionAndAnswer)).append("\n");
    sb.append("    purchaseOrderQuestionAndAnswer: ").append(toIndentedString(purchaseOrderQuestionAndAnswer)).append("\n");
    sb.append("    userDefinedQuestionAndAnswers: ").append(toIndentedString(userDefinedQuestionAndAnswers)).append("\n");
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

