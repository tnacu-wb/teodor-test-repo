package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.card.generated.models.payments.AmountDto;
import uk.co.whitbread.hotel.card.generated.models.payments.CardDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RefundDto
 */

@JsonTypeName("Refund")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RefundDto {

  private AmountDto amount;

  private CardDto card;

  private String reason;

  /**
   * Type of refund to process.
   */
  public enum TypeEnum {
    CARD("CARD"),
    
    PIBA("PIBA"),
    
    PIBA_EU("PIBA_EU"),
    
    PLANET_BASE_WALLETS_TEST("PLANET_BASE_WALLETS_TEST"),
    
    WALLET_APPLE("WALLET_APPLE"),
    
    WALLET_GOOGLE("WALLET_GOOGLE"),
    
    PAYPAL("PAYPAL"),
    
    WB_WALLETS_TEST("WB_WALLETS_TEST");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TypeEnum type;

  public RefundDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RefundDto(AmountDto amount, CardDto card, String reason, TypeEnum type) {
    this.amount = amount;
    this.card = card;
    this.reason = reason;
    this.type = type;
  }

  public RefundDto amount(AmountDto amount) {
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

  public RefundDto card(CardDto card) {
    this.card = card;
    return this;
  }

  /**
   * Get card
   * @return card
   */
  @NotNull @Valid 
  @Schema(name = "card", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("card")
  public CardDto getCard() {
    return card;
  }

  public void setCard(CardDto card) {
    this.card = card;
  }

  public RefundDto reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Reason for refund.
   * @return reason
   */
  @NotNull 
  @Schema(name = "reason", description = "Reason for refund.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reason")
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public RefundDto type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Type of refund to process.
   * @return type
   */
  @NotNull 
  @Schema(name = "type", example = "CARD", description = "Type of refund to process.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
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
    RefundDto refund = (RefundDto) o;
    return Objects.equals(this.amount, refund.amount) &&
        Objects.equals(this.card, refund.card) &&
        Objects.equals(this.reason, refund.reason) &&
        Objects.equals(this.type, refund.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, card, reason, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RefundDto {\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    card: ").append(toIndentedString(card)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
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

