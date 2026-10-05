package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BusinessAccountCnpDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessAccountCnpDto {

  private @Nullable String alcoholAllowed;

  private @Nullable Integer breakfastCodeReq;

  private @Nullable String carParkingAllowed;

  private @Nullable String cardNotPresentAuth;

  private @Nullable String customerReference;

  private @Nullable BigDecimal dinnerAllowance;

  private @Nullable String otherChargesAllowed;

  private @Nullable String purchaseOrder;

  private @Nullable String wifiAllowed;

  public BusinessAccountCnpDto alcoholAllowed(String alcoholAllowed) {
    this.alcoholAllowed = alcoholAllowed;
    return this;
  }

  /**
   * Get alcoholAllowed
   * @return alcoholAllowed
   */
  
  @Schema(name = "alcoholAllowed", example = "No", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alcoholAllowed")
  public String getAlcoholAllowed() {
    return alcoholAllowed;
  }

  public void setAlcoholAllowed(String alcoholAllowed) {
    this.alcoholAllowed = alcoholAllowed;
  }

  public BusinessAccountCnpDto breakfastCodeReq(Integer breakfastCodeReq) {
    this.breakfastCodeReq = breakfastCodeReq;
    return this;
  }

  /**
   * Get breakfastCodeReq
   * @return breakfastCodeReq
   */
  
  @Schema(name = "breakfastCodeReq", example = "12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("breakfastCodeReq")
  public Integer getBreakfastCodeReq() {
    return breakfastCodeReq;
  }

  public void setBreakfastCodeReq(Integer breakfastCodeReq) {
    this.breakfastCodeReq = breakfastCodeReq;
  }

  public BusinessAccountCnpDto carParkingAllowed(String carParkingAllowed) {
    this.carParkingAllowed = carParkingAllowed;
    return this;
  }

  /**
   * Get carParkingAllowed
   * @return carParkingAllowed
   */
  
  @Schema(name = "carParkingAllowed", example = "Yes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("carParkingAllowed")
  public String getCarParkingAllowed() {
    return carParkingAllowed;
  }

  public void setCarParkingAllowed(String carParkingAllowed) {
    this.carParkingAllowed = carParkingAllowed;
  }

  public BusinessAccountCnpDto cardNotPresentAuth(String cardNotPresentAuth) {
    this.cardNotPresentAuth = cardNotPresentAuth;
    return this;
  }

  /**
   * Get cardNotPresentAuth
   * @return cardNotPresentAuth
   */
  
  @Schema(name = "cardNotPresentAuth", example = "Yes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNotPresentAuth")
  public String getCardNotPresentAuth() {
    return cardNotPresentAuth;
  }

  public void setCardNotPresentAuth(String cardNotPresentAuth) {
    this.cardNotPresentAuth = cardNotPresentAuth;
  }

  public BusinessAccountCnpDto customerReference(String customerReference) {
    this.customerReference = customerReference;
    return this;
  }

  /**
   * Get customerReference
   * @return customerReference
   */
  
  @Schema(name = "customerReference", example = "ab12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReference")
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  public BusinessAccountCnpDto dinnerAllowance(BigDecimal dinnerAllowance) {
    this.dinnerAllowance = dinnerAllowance;
    return this;
  }

  /**
   * Get dinnerAllowance
   * @return dinnerAllowance
   */
  @Valid 
  @Schema(name = "dinnerAllowance", example = "50", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dinnerAllowance")
  public BigDecimal getDinnerAllowance() {
    return dinnerAllowance;
  }

  public void setDinnerAllowance(BigDecimal dinnerAllowance) {
    this.dinnerAllowance = dinnerAllowance;
  }

  public BusinessAccountCnpDto otherChargesAllowed(String otherChargesAllowed) {
    this.otherChargesAllowed = otherChargesAllowed;
    return this;
  }

  /**
   * Get otherChargesAllowed
   * @return otherChargesAllowed
   */
  
  @Schema(name = "otherChargesAllowed", example = "No", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("otherChargesAllowed")
  public String getOtherChargesAllowed() {
    return otherChargesAllowed;
  }

  public void setOtherChargesAllowed(String otherChargesAllowed) {
    this.otherChargesAllowed = otherChargesAllowed;
  }

  public BusinessAccountCnpDto purchaseOrder(String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
    return this;
  }

  /**
   * Get purchaseOrder
   * @return purchaseOrder
   */
  
  @Schema(name = "purchaseOrder", example = "abc123", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrder")
  public String getPurchaseOrder() {
    return purchaseOrder;
  }

  public void setPurchaseOrder(String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
  }

  public BusinessAccountCnpDto wifiAllowed(String wifiAllowed) {
    this.wifiAllowed = wifiAllowed;
    return this;
  }

  /**
   * Get wifiAllowed
   * @return wifiAllowed
   */
  
  @Schema(name = "wifiAllowed", example = "Yes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("wifiAllowed")
  public String getWifiAllowed() {
    return wifiAllowed;
  }

  public void setWifiAllowed(String wifiAllowed) {
    this.wifiAllowed = wifiAllowed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessAccountCnpDto businessAccountCnpDto = (BusinessAccountCnpDto) o;
    return Objects.equals(this.alcoholAllowed, businessAccountCnpDto.alcoholAllowed) &&
        Objects.equals(this.breakfastCodeReq, businessAccountCnpDto.breakfastCodeReq) &&
        Objects.equals(this.carParkingAllowed, businessAccountCnpDto.carParkingAllowed) &&
        Objects.equals(this.cardNotPresentAuth, businessAccountCnpDto.cardNotPresentAuth) &&
        Objects.equals(this.customerReference, businessAccountCnpDto.customerReference) &&
        Objects.equals(this.dinnerAllowance, businessAccountCnpDto.dinnerAllowance) &&
        Objects.equals(this.otherChargesAllowed, businessAccountCnpDto.otherChargesAllowed) &&
        Objects.equals(this.purchaseOrder, businessAccountCnpDto.purchaseOrder) &&
        Objects.equals(this.wifiAllowed, businessAccountCnpDto.wifiAllowed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alcoholAllowed, breakfastCodeReq, carParkingAllowed, cardNotPresentAuth, customerReference, dinnerAllowance, otherChargesAllowed, purchaseOrder, wifiAllowed);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessAccountCnpDto {\n");
    sb.append("    alcoholAllowed: ").append(toIndentedString(alcoholAllowed)).append("\n");
    sb.append("    breakfastCodeReq: ").append(toIndentedString(breakfastCodeReq)).append("\n");
    sb.append("    carParkingAllowed: ").append(toIndentedString(carParkingAllowed)).append("\n");
    sb.append("    cardNotPresentAuth: ").append(toIndentedString(cardNotPresentAuth)).append("\n");
    sb.append("    customerReference: ").append(toIndentedString(customerReference)).append("\n");
    sb.append("    dinnerAllowance: ").append(toIndentedString(dinnerAllowance)).append("\n");
    sb.append("    otherChargesAllowed: ").append(toIndentedString(otherChargesAllowed)).append("\n");
    sb.append("    purchaseOrder: ").append(toIndentedString(purchaseOrder)).append("\n");
    sb.append("    wifiAllowed: ").append(toIndentedString(wifiAllowed)).append("\n");
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

