package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AmountDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BillingDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BusinessItemsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CardDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ScaDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentDto {

  private @Nullable AmountDto amount;

  private @Nullable BillingDto billing;

  private @Nullable BusinessItemsDto businessItems;

  private @Nullable CardDto card;

  private @Nullable String environment;

  private @Nullable String paypalDeviceData;

  private @Nullable String paypalNonce;

  private @Nullable Boolean pibaCardPresent;

  private @Nullable ScaDto sca;

  private String subType;

  private String type;

  public PaymentDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentDto(String subType, String type) {
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
  @Valid 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
  @Valid 
  @Schema(name = "billing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("billing")
  public BillingDto getBilling() {
    return billing;
  }

  public void setBilling(BillingDto billing) {
    this.billing = billing;
  }

  public PaymentDto businessItems(BusinessItemsDto businessItems) {
    this.businessItems = businessItems;
    return this;
  }

  /**
   * Get businessItems
   * @return businessItems
   */
  @Valid 
  @Schema(name = "businessItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessItems")
  public BusinessItemsDto getBusinessItems() {
    return businessItems;
  }

  public void setBusinessItems(BusinessItemsDto businessItems) {
    this.businessItems = businessItems;
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

  public PaymentDto environment(String environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Get environment
   * @return environment
   */
  
  @Schema(name = "environment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("environment")
  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public PaymentDto paypalDeviceData(String paypalDeviceData) {
    this.paypalDeviceData = paypalDeviceData;
    return this;
  }

  /**
   * Get paypalDeviceData
   * @return paypalDeviceData
   */
  
  @Schema(name = "paypalDeviceData", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paypalDeviceData")
  public String getPaypalDeviceData() {
    return paypalDeviceData;
  }

  public void setPaypalDeviceData(String paypalDeviceData) {
    this.paypalDeviceData = paypalDeviceData;
  }

  public PaymentDto paypalNonce(String paypalNonce) {
    this.paypalNonce = paypalNonce;
    return this;
  }

  /**
   * Get paypalNonce
   * @return paypalNonce
   */
  
  @Schema(name = "paypalNonce", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paypalNonce")
  public String getPaypalNonce() {
    return paypalNonce;
  }

  public void setPaypalNonce(String paypalNonce) {
    this.paypalNonce = paypalNonce;
  }

  public PaymentDto pibaCardPresent(Boolean pibaCardPresent) {
    this.pibaCardPresent = pibaCardPresent;
    return this;
  }

  /**
   * Get pibaCardPresent
   * @return pibaCardPresent
   */
  
  @Schema(name = "pibaCardPresent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pibaCardPresent")
  public Boolean getPibaCardPresent() {
    return pibaCardPresent;
  }

  public void setPibaCardPresent(Boolean pibaCardPresent) {
    this.pibaCardPresent = pibaCardPresent;
  }

  public PaymentDto sca(ScaDto sca) {
    this.sca = sca;
    return this;
  }

  /**
   * Get sca
   * @return sca
   */
  @Valid 
  @Schema(name = "sca", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sca")
  public ScaDto getSca() {
    return sca;
  }

  public void setSca(ScaDto sca) {
    this.sca = sca;
  }

  public PaymentDto subType(String subType) {
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

  public PaymentDto type(String type) {
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
    PaymentDto paymentDto = (PaymentDto) o;
    return Objects.equals(this.amount, paymentDto.amount) &&
        Objects.equals(this.billing, paymentDto.billing) &&
        Objects.equals(this.businessItems, paymentDto.businessItems) &&
        Objects.equals(this.card, paymentDto.card) &&
        Objects.equals(this.environment, paymentDto.environment) &&
        Objects.equals(this.paypalDeviceData, paymentDto.paypalDeviceData) &&
        Objects.equals(this.paypalNonce, paymentDto.paypalNonce) &&
        Objects.equals(this.pibaCardPresent, paymentDto.pibaCardPresent) &&
        Objects.equals(this.sca, paymentDto.sca) &&
        Objects.equals(this.subType, paymentDto.subType) &&
        Objects.equals(this.type, paymentDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, billing, businessItems, card, environment, paypalDeviceData, paypalNonce, pibaCardPresent, sca, subType, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentDto {\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
    sb.append("    businessItems: ").append(toIndentedString(businessItems)).append("\n");
    sb.append("    card: ").append(toIndentedString(card)).append("\n");
    sb.append("    environment: ").append(toIndentedString(environment)).append("\n");
    sb.append("    paypalDeviceData: ").append(toIndentedString(paypalDeviceData)).append("\n");
    sb.append("    paypalNonce: ").append(toIndentedString(paypalNonce)).append("\n");
    sb.append("    pibaCardPresent: ").append(toIndentedString(pibaCardPresent)).append("\n");
    sb.append("    sca: ").append(toIndentedString(sca)).append("\n");
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

