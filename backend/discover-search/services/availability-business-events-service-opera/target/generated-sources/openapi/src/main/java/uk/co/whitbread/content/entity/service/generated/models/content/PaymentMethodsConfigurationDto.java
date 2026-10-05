package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentMethodsConfigurationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentMethodsConfigurationDto {

  private @Nullable String code;

  private @Nullable String listOrder;

  private @Nullable String logo;

  private @Nullable String name;

  private @Nullable String operaPaymentMethod;

  private @Nullable String supportedBookingTypes;

  private @Nullable String supportedCards;

  private @Nullable String supportedChannels;

  public PaymentMethodsConfigurationDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public PaymentMethodsConfigurationDto listOrder(String listOrder) {
    this.listOrder = listOrder;
    return this;
  }

  /**
   * Get listOrder
   * @return listOrder
   */
  
  @Schema(name = "listOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("listOrder")
  public String getListOrder() {
    return listOrder;
  }

  public void setListOrder(String listOrder) {
    this.listOrder = listOrder;
  }

  public PaymentMethodsConfigurationDto logo(String logo) {
    this.logo = logo;
    return this;
  }

  /**
   * Get logo
   * @return logo
   */
  
  @Schema(name = "logo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logo")
  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public PaymentMethodsConfigurationDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PaymentMethodsConfigurationDto operaPaymentMethod(String operaPaymentMethod) {
    this.operaPaymentMethod = operaPaymentMethod;
    return this;
  }

  /**
   * Get operaPaymentMethod
   * @return operaPaymentMethod
   */
  
  @Schema(name = "operaPaymentMethod", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operaPaymentMethod")
  public String getOperaPaymentMethod() {
    return operaPaymentMethod;
  }

  public void setOperaPaymentMethod(String operaPaymentMethod) {
    this.operaPaymentMethod = operaPaymentMethod;
  }

  public PaymentMethodsConfigurationDto supportedBookingTypes(String supportedBookingTypes) {
    this.supportedBookingTypes = supportedBookingTypes;
    return this;
  }

  /**
   * Get supportedBookingTypes
   * @return supportedBookingTypes
   */
  
  @Schema(name = "supportedBookingTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("supportedBookingTypes")
  public String getSupportedBookingTypes() {
    return supportedBookingTypes;
  }

  public void setSupportedBookingTypes(String supportedBookingTypes) {
    this.supportedBookingTypes = supportedBookingTypes;
  }

  public PaymentMethodsConfigurationDto supportedCards(String supportedCards) {
    this.supportedCards = supportedCards;
    return this;
  }

  /**
   * Get supportedCards
   * @return supportedCards
   */
  
  @Schema(name = "supportedCards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("supportedCards")
  public String getSupportedCards() {
    return supportedCards;
  }

  public void setSupportedCards(String supportedCards) {
    this.supportedCards = supportedCards;
  }

  public PaymentMethodsConfigurationDto supportedChannels(String supportedChannels) {
    this.supportedChannels = supportedChannels;
    return this;
  }

  /**
   * Get supportedChannels
   * @return supportedChannels
   */
  
  @Schema(name = "supportedChannels", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("supportedChannels")
  public String getSupportedChannels() {
    return supportedChannels;
  }

  public void setSupportedChannels(String supportedChannels) {
    this.supportedChannels = supportedChannels;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentMethodsConfigurationDto paymentMethodsConfigurationDto = (PaymentMethodsConfigurationDto) o;
    return Objects.equals(this.code, paymentMethodsConfigurationDto.code) &&
        Objects.equals(this.listOrder, paymentMethodsConfigurationDto.listOrder) &&
        Objects.equals(this.logo, paymentMethodsConfigurationDto.logo) &&
        Objects.equals(this.name, paymentMethodsConfigurationDto.name) &&
        Objects.equals(this.operaPaymentMethod, paymentMethodsConfigurationDto.operaPaymentMethod) &&
        Objects.equals(this.supportedBookingTypes, paymentMethodsConfigurationDto.supportedBookingTypes) &&
        Objects.equals(this.supportedCards, paymentMethodsConfigurationDto.supportedCards) &&
        Objects.equals(this.supportedChannels, paymentMethodsConfigurationDto.supportedChannels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, listOrder, logo, name, operaPaymentMethod, supportedBookingTypes, supportedCards, supportedChannels);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentMethodsConfigurationDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    listOrder: ").append(toIndentedString(listOrder)).append("\n");
    sb.append("    logo: ").append(toIndentedString(logo)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    operaPaymentMethod: ").append(toIndentedString(operaPaymentMethod)).append("\n");
    sb.append("    supportedBookingTypes: ").append(toIndentedString(supportedBookingTypes)).append("\n");
    sb.append("    supportedCards: ").append(toIndentedString(supportedCards)).append("\n");
    sb.append("    supportedChannels: ").append(toIndentedString(supportedChannels)).append("\n");
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

