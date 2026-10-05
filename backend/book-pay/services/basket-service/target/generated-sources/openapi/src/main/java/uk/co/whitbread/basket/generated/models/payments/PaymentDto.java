package uk.co.whitbread.basket.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.payments.AmountDto;
import uk.co.whitbread.basket.generated.models.payments.BillingDto;
import uk.co.whitbread.basket.generated.models.payments.CardDto;
import uk.co.whitbread.basket.generated.models.payments.MitDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@JsonTypeName("Payment")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:02.841275+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentDto {

  private AmountDto amount;

  private BillingDto billing;

  private @Nullable CardDto card;

  private @Nullable Boolean cardPresent;

  private @Nullable String environment;

  private @Nullable MitDto mit;

  private @Nullable String settlementReference;

  private @Nullable String paypalNonce;

  private @Nullable String paypalDeviceData;

  /**
   * Sub type of payment to process.
   */
  public enum SubTypeEnum {
    ECOMM("ECOMM"),
    
    MOTO("MOTO"),
    
    ECKOH("ECKOH"),
    
    PAYPAL("PAYPAL"),
    
    MIT("MIT"),
    
    SAVE_CARD("SAVE_CARD"),
    
    AUTHORIZE_CARD("AUTHORIZE_CARD"),
    
    SECURE_BOOKING("SECURE_BOOKING"),
    
    MIT_CC("MIT_CC");

    private String value;

    SubTypeEnum(String value) {
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
    public static SubTypeEnum fromValue(String value) {
      for (SubTypeEnum b : SubTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private SubTypeEnum subType;

  /**
   * Type of payment to process.
   */
  public enum TypeEnum {
    CARD("CARD"),
    
    PIBA("PIBA"),
    
    PIBA_EU("PIBA_EU"),
    
    PAYPAL("PAYPAL"),
    
    WALLET_APPLE("WALLET_APPLE"),
    
    WALLET_GOOGLE("WALLET_GOOGLE");

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

  public PaymentDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentDto(AmountDto amount, BillingDto billing, SubTypeEnum subType, TypeEnum type) {
    this.amount = amount;
    this.billing = billing;
    this.subType = subType;
    this.type = type;
  }

  public PaymentDto amount(AmountDto amount) {
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

  public PaymentDto billing(BillingDto billing) {
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
  public BillingDto getBilling() {
    return billing;
  }

  public void setBilling(BillingDto billing) {
    this.billing = billing;
  }

  public PaymentDto card(CardDto card) {
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

  public PaymentDto cardPresent(Boolean cardPresent) {
    this.cardPresent = cardPresent;
    return this;
  }

  /**
   * Get cardPresent
   * @return cardPresent
   */
  
  @Schema(name = "cardPresent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardPresent")
  public Boolean getCardPresent() {
    return cardPresent;
  }

  public void setCardPresent(Boolean cardPresent) {
    this.cardPresent = cardPresent;
  }

  public PaymentDto environment(String environment) {
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

  public PaymentDto mit(MitDto mit) {
    this.mit = mit;
    return this;
  }

  /**
   * Get mit
   * @return mit
   */
  @Valid 
  @Schema(name = "mit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mit")
  public MitDto getMit() {
    return mit;
  }

  public void setMit(MitDto mit) {
    this.mit = mit;
  }

  public PaymentDto settlementReference(String settlementReference) {
    this.settlementReference = settlementReference;
    return this;
  }

  /**
   * 18-digit BART reference used in PAY_NOW transactions only
   * @return settlementReference
   */
  
  @Schema(name = "settlementReference", example = "123456789012345678", description = "18-digit BART reference used in PAY_NOW transactions only", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("settlementReference")
  public String getSettlementReference() {
    return settlementReference;
  }

  public void setSettlementReference(String settlementReference) {
    this.settlementReference = settlementReference;
  }

  public PaymentDto paypalNonce(String paypalNonce) {
    this.paypalNonce = paypalNonce;
    return this;
  }

  /**
   * Nonce string receive from UI after Paypal Authorization
   * @return paypalNonce
   */
  
  @Schema(name = "paypalNonce", example = "c0737cdb-1ae8-11df-4a31-71e6702fb072", description = "Nonce string receive from UI after Paypal Authorization", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paypalNonce")
  public String getPaypalNonce() {
    return paypalNonce;
  }

  public void setPaypalNonce(String paypalNonce) {
    this.paypalNonce = paypalNonce;
  }

  public PaymentDto paypalDeviceData(String paypalDeviceData) {
    this.paypalDeviceData = paypalDeviceData;
    return this;
  }

  /**
   * String generated by the Paypal SDK in the UI from the device making the payment
   * @return paypalDeviceData
   */
  
  @Schema(name = "paypalDeviceData", example = "{\\\"correlation_id\\\":\\\"5792b50d35525fd075fbdc132b62c986\\\"}", description = "String generated by the Paypal SDK in the UI from the device making the payment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paypalDeviceData")
  public String getPaypalDeviceData() {
    return paypalDeviceData;
  }

  public void setPaypalDeviceData(String paypalDeviceData) {
    this.paypalDeviceData = paypalDeviceData;
  }

  public PaymentDto subType(SubTypeEnum subType) {
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
  public SubTypeEnum getSubType() {
    return subType;
  }

  public void setSubType(SubTypeEnum subType) {
    this.subType = subType;
  }

  public PaymentDto type(TypeEnum type) {
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
    PaymentDto payment = (PaymentDto) o;
    return Objects.equals(this.amount, payment.amount) &&
        Objects.equals(this.billing, payment.billing) &&
        Objects.equals(this.card, payment.card) &&
        Objects.equals(this.cardPresent, payment.cardPresent) &&
        Objects.equals(this.environment, payment.environment) &&
        Objects.equals(this.mit, payment.mit) &&
        Objects.equals(this.settlementReference, payment.settlementReference) &&
        Objects.equals(this.paypalNonce, payment.paypalNonce) &&
        Objects.equals(this.paypalDeviceData, payment.paypalDeviceData) &&
        Objects.equals(this.subType, payment.subType) &&
        Objects.equals(this.type, payment.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, billing, card, cardPresent, environment, mit, settlementReference, paypalNonce, paypalDeviceData, subType, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentDto {\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
    sb.append("    card: ").append(toIndentedString(card)).append("\n");
    sb.append("    cardPresent: ").append(toIndentedString(cardPresent)).append("\n");
    sb.append("    environment: ").append(toIndentedString(environment)).append("\n");
    sb.append("    mit: ").append(toIndentedString(mit)).append("\n");
    sb.append("    settlementReference: ").append(toIndentedString(settlementReference)).append("\n");
    sb.append("    paypalNonce: ").append(toIndentedString(paypalNonce)).append("\n");
    sb.append("    paypalDeviceData: ").append(toIndentedString(paypalDeviceData)).append("\n");
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

