package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.refund.processor.generated.models.payments.AmountDto;
import uk.co.whitbread.refund.processor.generated.models.payments.BillingResponseDto;
import uk.co.whitbread.refund.processor.generated.models.payments.CardDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentRsDto
 */

@JsonTypeName("PaymentRs")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentRsDto {

  private String type;

  private String subType;

  private @Nullable String environment;

  private @Nullable CardDto card;

  private AmountDto amount;

  private BillingResponseDto billing;

  public PaymentRsDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentRsDto(String type, String subType, AmountDto amount, BillingResponseDto billing) {
    this.type = type;
    this.subType = subType;
    this.amount = amount;
    this.billing = billing;
  }

  public PaymentRsDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Type of payment to process.
   * @return type
   */
  @NotNull 
  @Schema(name = "type", example = "CARD", description = "Type of payment to process.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public PaymentRsDto subType(String subType) {
    this.subType = subType;
    return this;
  }

  /**
   * Sub type of payment to process.
   * @return subType
   */
  @NotNull 
  @Schema(name = "subType", example = "ECOMM", description = "Sub type of payment to process.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("subType")
  public String getSubType() {
    return subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
  }

  public PaymentRsDto environment(String environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Browser window host name where ECOMM payment is being processed via iPage.
   * @return environment
   */
  
  @Schema(name = "environment", example = "https://www.premierinn.com", description = "Browser window host name where ECOMM payment is being processed via iPage.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("environment")
  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public PaymentRsDto card(CardDto card) {
    this.card = card;
    return this;
  }

  /**
   * Get card
   * @return card
   */
  @Valid 
  @Schema(name = "card", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("card")
  public CardDto getCard() {
    return card;
  }

  public void setCard(CardDto card) {
    this.card = card;
  }

  public PaymentRsDto amount(AmountDto amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
   */
  @NotNull @Valid 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public AmountDto getAmount() {
    return amount;
  }

  public void setAmount(AmountDto amount) {
    this.amount = amount;
  }

  public PaymentRsDto billing(BillingResponseDto billing) {
    this.billing = billing;
    return this;
  }

  /**
   * Get billing
   * @return billing
   */
  @NotNull @Valid 
  @Schema(name = "billing", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("billing")
  public BillingResponseDto getBilling() {
    return billing;
  }

  public void setBilling(BillingResponseDto billing) {
    this.billing = billing;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentRsDto paymentRs = (PaymentRsDto) o;
    return Objects.equals(this.type, paymentRs.type) &&
        Objects.equals(this.subType, paymentRs.subType) &&
        Objects.equals(this.environment, paymentRs.environment) &&
        Objects.equals(this.card, paymentRs.card) &&
        Objects.equals(this.amount, paymentRs.amount) &&
        Objects.equals(this.billing, paymentRs.billing);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, subType, environment, card, amount, billing);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentRsDto {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    subType: ").append(toIndentedString(subType)).append("\n");
    sb.append("    environment: ").append(toIndentedString(environment)).append("\n");
    sb.append("    card: ").append(toIndentedString(card)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
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

