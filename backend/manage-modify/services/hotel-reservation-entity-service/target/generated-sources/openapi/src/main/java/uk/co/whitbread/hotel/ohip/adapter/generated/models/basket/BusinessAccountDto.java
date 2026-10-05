package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BusinessAccountDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessAccountDto {

  private @Nullable String alcoholAllowed;

  private @Nullable Integer breakfastCodeReq;

  private @Nullable String carParkingAllowed;

  private @Nullable String cardNotPresentAuth;

  private @Nullable String customerReference;

  private @Nullable BigDecimal dinnerAllowance;

  private @Nullable String otherChargesAllowed;

  private @Nullable String purchaseOrder;

  private @Nullable String wifiAllowed;

  public BusinessAccountDto alcoholAllowed(String alcoholAllowed) {
    this.alcoholAllowed = alcoholAllowed;
    return this;
  }

  /**
   * Get alcoholAllowed
   * @return alcoholAllowed
   */
  
  @Schema(name = "alcoholAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alcoholAllowed")
  public String getAlcoholAllowed() {
    return alcoholAllowed;
  }

  public void setAlcoholAllowed(String alcoholAllowed) {
    this.alcoholAllowed = alcoholAllowed;
  }

  public BusinessAccountDto breakfastCodeReq(Integer breakfastCodeReq) {
    this.breakfastCodeReq = breakfastCodeReq;
    return this;
  }

  /**
   * Get breakfastCodeReq
   * @return breakfastCodeReq
   */
  
  @Schema(name = "breakfastCodeReq", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("breakfastCodeReq")
  public Integer getBreakfastCodeReq() {
    return breakfastCodeReq;
  }

  public void setBreakfastCodeReq(Integer breakfastCodeReq) {
    this.breakfastCodeReq = breakfastCodeReq;
  }

  public BusinessAccountDto carParkingAllowed(String carParkingAllowed) {
    this.carParkingAllowed = carParkingAllowed;
    return this;
  }

  /**
   * Get carParkingAllowed
   * @return carParkingAllowed
   */
  
  @Schema(name = "carParkingAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("carParkingAllowed")
  public String getCarParkingAllowed() {
    return carParkingAllowed;
  }

  public void setCarParkingAllowed(String carParkingAllowed) {
    this.carParkingAllowed = carParkingAllowed;
  }

  public BusinessAccountDto cardNotPresentAuth(String cardNotPresentAuth) {
    this.cardNotPresentAuth = cardNotPresentAuth;
    return this;
  }

  /**
   * Get cardNotPresentAuth
   * @return cardNotPresentAuth
   */
  
  @Schema(name = "cardNotPresentAuth", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNotPresentAuth")
  public String getCardNotPresentAuth() {
    return cardNotPresentAuth;
  }

  public void setCardNotPresentAuth(String cardNotPresentAuth) {
    this.cardNotPresentAuth = cardNotPresentAuth;
  }

  public BusinessAccountDto customerReference(String customerReference) {
    this.customerReference = customerReference;
    return this;
  }

  /**
   * Get customerReference
   * @return customerReference
   */
  
  @Schema(name = "customerReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReference")
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  public BusinessAccountDto dinnerAllowance(BigDecimal dinnerAllowance) {
    this.dinnerAllowance = dinnerAllowance;
    return this;
  }

  /**
   * Get dinnerAllowance
   * @return dinnerAllowance
   */
  @Valid 
  @Schema(name = "dinnerAllowance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dinnerAllowance")
  public BigDecimal getDinnerAllowance() {
    return dinnerAllowance;
  }

  public void setDinnerAllowance(BigDecimal dinnerAllowance) {
    this.dinnerAllowance = dinnerAllowance;
  }

  public BusinessAccountDto otherChargesAllowed(String otherChargesAllowed) {
    this.otherChargesAllowed = otherChargesAllowed;
    return this;
  }

  /**
   * Get otherChargesAllowed
   * @return otherChargesAllowed
   */
  
  @Schema(name = "otherChargesAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("otherChargesAllowed")
  public String getOtherChargesAllowed() {
    return otherChargesAllowed;
  }

  public void setOtherChargesAllowed(String otherChargesAllowed) {
    this.otherChargesAllowed = otherChargesAllowed;
  }

  public BusinessAccountDto purchaseOrder(String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
    return this;
  }

  /**
   * Get purchaseOrder
   * @return purchaseOrder
   */
  
  @Schema(name = "purchaseOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrder")
  public String getPurchaseOrder() {
    return purchaseOrder;
  }

  public void setPurchaseOrder(String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
  }

  public BusinessAccountDto wifiAllowed(String wifiAllowed) {
    this.wifiAllowed = wifiAllowed;
    return this;
  }

  /**
   * Get wifiAllowed
   * @return wifiAllowed
   */
  
  @Schema(name = "wifiAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    BusinessAccountDto businessAccountDto = (BusinessAccountDto) o;
    return Objects.equals(this.alcoholAllowed, businessAccountDto.alcoholAllowed) &&
        Objects.equals(this.breakfastCodeReq, businessAccountDto.breakfastCodeReq) &&
        Objects.equals(this.carParkingAllowed, businessAccountDto.carParkingAllowed) &&
        Objects.equals(this.cardNotPresentAuth, businessAccountDto.cardNotPresentAuth) &&
        Objects.equals(this.customerReference, businessAccountDto.customerReference) &&
        Objects.equals(this.dinnerAllowance, businessAccountDto.dinnerAllowance) &&
        Objects.equals(this.otherChargesAllowed, businessAccountDto.otherChargesAllowed) &&
        Objects.equals(this.purchaseOrder, businessAccountDto.purchaseOrder) &&
        Objects.equals(this.wifiAllowed, businessAccountDto.wifiAllowed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alcoholAllowed, breakfastCodeReq, carParkingAllowed, cardNotPresentAuth, customerReference, dinnerAllowance, otherChargesAllowed, purchaseOrder, wifiAllowed);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessAccountDto {\n");
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

