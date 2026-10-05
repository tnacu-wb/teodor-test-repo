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
 * PaymentMethodDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentMethodDto {

  private @Nullable String code;

  private @Nullable String feeAmount;

  private @Nullable String feeCurrency;

  private @Nullable String listOrder;

  private @Nullable String name;

  private @Nullable Boolean paymentOnly;

  private @Nullable String schemeLogo;

  public PaymentMethodDto code(String code) {
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

  public PaymentMethodDto feeAmount(String feeAmount) {
    this.feeAmount = feeAmount;
    return this;
  }

  /**
   * Get feeAmount
   * @return feeAmount
   */
  
  @Schema(name = "feeAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("feeAmount")
  public String getFeeAmount() {
    return feeAmount;
  }

  public void setFeeAmount(String feeAmount) {
    this.feeAmount = feeAmount;
  }

  public PaymentMethodDto feeCurrency(String feeCurrency) {
    this.feeCurrency = feeCurrency;
    return this;
  }

  /**
   * Get feeCurrency
   * @return feeCurrency
   */
  
  @Schema(name = "feeCurrency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("feeCurrency")
  public String getFeeCurrency() {
    return feeCurrency;
  }

  public void setFeeCurrency(String feeCurrency) {
    this.feeCurrency = feeCurrency;
  }

  public PaymentMethodDto listOrder(String listOrder) {
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

  public PaymentMethodDto name(String name) {
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

  public PaymentMethodDto paymentOnly(Boolean paymentOnly) {
    this.paymentOnly = paymentOnly;
    return this;
  }

  /**
   * Get paymentOnly
   * @return paymentOnly
   */
  
  @Schema(name = "paymentOnly", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentOnly")
  public Boolean getPaymentOnly() {
    return paymentOnly;
  }

  public void setPaymentOnly(Boolean paymentOnly) {
    this.paymentOnly = paymentOnly;
  }

  public PaymentMethodDto schemeLogo(String schemeLogo) {
    this.schemeLogo = schemeLogo;
    return this;
  }

  /**
   * Get schemeLogo
   * @return schemeLogo
   */
  
  @Schema(name = "schemeLogo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("schemeLogo")
  public String getSchemeLogo() {
    return schemeLogo;
  }

  public void setSchemeLogo(String schemeLogo) {
    this.schemeLogo = schemeLogo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentMethodDto paymentMethodDto = (PaymentMethodDto) o;
    return Objects.equals(this.code, paymentMethodDto.code) &&
        Objects.equals(this.feeAmount, paymentMethodDto.feeAmount) &&
        Objects.equals(this.feeCurrency, paymentMethodDto.feeCurrency) &&
        Objects.equals(this.listOrder, paymentMethodDto.listOrder) &&
        Objects.equals(this.name, paymentMethodDto.name) &&
        Objects.equals(this.paymentOnly, paymentMethodDto.paymentOnly) &&
        Objects.equals(this.schemeLogo, paymentMethodDto.schemeLogo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, feeAmount, feeCurrency, listOrder, name, paymentOnly, schemeLogo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentMethodDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    feeAmount: ").append(toIndentedString(feeAmount)).append("\n");
    sb.append("    feeCurrency: ").append(toIndentedString(feeCurrency)).append("\n");
    sb.append("    listOrder: ").append(toIndentedString(listOrder)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    paymentOnly: ").append(toIndentedString(paymentOnly)).append("\n");
    sb.append("    schemeLogo: ").append(toIndentedString(schemeLogo)).append("\n");
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

