package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.BillingCcuiDto;
import uk.co.whitbread.hotel.generated.models.basket.CardCcuiDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentCcuiDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentCcuiDto {

  private @Nullable BillingCcuiDto billing;

  private @Nullable CardCcuiDto card;

  private String subType;

  private String type;

  public PaymentCcuiDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentCcuiDto(String subType, String type) {
    this.subType = subType;
    this.type = type;
  }

  public PaymentCcuiDto billing(BillingCcuiDto billing) {
    this.billing = billing;
    return this;
  }

  /**
   * Get billing
   * @return billing
   */
  @Valid 
  @Schema(name = "billing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("billing")
  public BillingCcuiDto getBilling() {
    return billing;
  }

  public void setBilling(BillingCcuiDto billing) {
    this.billing = billing;
  }

  public PaymentCcuiDto card(CardCcuiDto card) {
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
  public CardCcuiDto getCard() {
    return card;
  }

  public void setCard(CardCcuiDto card) {
    this.card = card;
  }

  public PaymentCcuiDto subType(String subType) {
    this.subType = subType;
    return this;
  }

  /**
   * Get subType
   * @return subType
   */
  @NotNull 
  @Schema(name = "subType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("subType")
  public String getSubType() {
    return subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
  }

  public PaymentCcuiDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  @NotNull 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentCcuiDto paymentCcuiDto = (PaymentCcuiDto) o;
    return Objects.equals(this.billing, paymentCcuiDto.billing) &&
        Objects.equals(this.card, paymentCcuiDto.card) &&
        Objects.equals(this.subType, paymentCcuiDto.subType) &&
        Objects.equals(this.type, paymentCcuiDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(billing, card, subType, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentCcuiDto {\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
    sb.append("    card: ").append(toIndentedString(card)).append("\n");
    sb.append("    subType: ").append(toIndentedString(subType)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

