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
 * AcceptedCreditCardDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AcceptedCreditCardDto {

  private @Nullable String code;

  private @Nullable String code3CP;

  private @Nullable String codeOpera;

  private @Nullable String codeOperaCardType;

  private @Nullable String feeAmount;

  private @Nullable String feeCurrency;

  private @Nullable String listOrder;

  private @Nullable String name;

  private @Nullable Boolean paymentOnly;

  private @Nullable String schemeLogo;

  public AcceptedCreditCardDto code(String code) {
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

  public AcceptedCreditCardDto code3CP(String code3CP) {
    this.code3CP = code3CP;
    return this;
  }

  /**
   * Get code3CP
   * @return code3CP
   */
  
  @Schema(name = "code3CP", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code3CP")
  public String getCode3CP() {
    return code3CP;
  }

  public void setCode3CP(String code3CP) {
    this.code3CP = code3CP;
  }

  public AcceptedCreditCardDto codeOpera(String codeOpera) {
    this.codeOpera = codeOpera;
    return this;
  }

  /**
   * Get codeOpera
   * @return codeOpera
   */
  
  @Schema(name = "codeOpera", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("codeOpera")
  public String getCodeOpera() {
    return codeOpera;
  }

  public void setCodeOpera(String codeOpera) {
    this.codeOpera = codeOpera;
  }

  public AcceptedCreditCardDto codeOperaCardType(String codeOperaCardType) {
    this.codeOperaCardType = codeOperaCardType;
    return this;
  }

  /**
   * Get codeOperaCardType
   * @return codeOperaCardType
   */
  
  @Schema(name = "codeOperaCardType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("codeOperaCardType")
  public String getCodeOperaCardType() {
    return codeOperaCardType;
  }

  public void setCodeOperaCardType(String codeOperaCardType) {
    this.codeOperaCardType = codeOperaCardType;
  }

  public AcceptedCreditCardDto feeAmount(String feeAmount) {
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

  public AcceptedCreditCardDto feeCurrency(String feeCurrency) {
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

  public AcceptedCreditCardDto listOrder(String listOrder) {
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

  public AcceptedCreditCardDto name(String name) {
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

  public AcceptedCreditCardDto paymentOnly(Boolean paymentOnly) {
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

  public AcceptedCreditCardDto schemeLogo(String schemeLogo) {
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
    AcceptedCreditCardDto acceptedCreditCardDto = (AcceptedCreditCardDto) o;
    return Objects.equals(this.code, acceptedCreditCardDto.code) &&
        Objects.equals(this.code3CP, acceptedCreditCardDto.code3CP) &&
        Objects.equals(this.codeOpera, acceptedCreditCardDto.codeOpera) &&
        Objects.equals(this.codeOperaCardType, acceptedCreditCardDto.codeOperaCardType) &&
        Objects.equals(this.feeAmount, acceptedCreditCardDto.feeAmount) &&
        Objects.equals(this.feeCurrency, acceptedCreditCardDto.feeCurrency) &&
        Objects.equals(this.listOrder, acceptedCreditCardDto.listOrder) &&
        Objects.equals(this.name, acceptedCreditCardDto.name) &&
        Objects.equals(this.paymentOnly, acceptedCreditCardDto.paymentOnly) &&
        Objects.equals(this.schemeLogo, acceptedCreditCardDto.schemeLogo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, code3CP, codeOpera, codeOperaCardType, feeAmount, feeCurrency, listOrder, name, paymentOnly, schemeLogo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AcceptedCreditCardDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    code3CP: ").append(toIndentedString(code3CP)).append("\n");
    sb.append("    codeOpera: ").append(toIndentedString(codeOpera)).append("\n");
    sb.append("    codeOperaCardType: ").append(toIndentedString(codeOperaCardType)).append("\n");
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

