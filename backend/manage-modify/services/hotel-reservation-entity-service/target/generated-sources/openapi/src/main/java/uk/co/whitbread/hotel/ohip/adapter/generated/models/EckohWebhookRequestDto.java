package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * EckohWebhookRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EckohWebhookRequestDto {

  private @Nullable String exception;

  private String expiry;

  private String maskedPan;

  private String paymentId;

  private String reference;

  private String result;

  private Integer resultCode;

  private String scheme;

  private String token;

  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    CREDIT("credit"),
    
    DEBIT("debit"),
    
    PREPAID("prepaid");

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

  public EckohWebhookRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EckohWebhookRequestDto(String expiry, String maskedPan, String paymentId, String reference, String result, Integer resultCode, String scheme, String token, TypeEnum type) {
    this.expiry = expiry;
    this.maskedPan = maskedPan;
    this.paymentId = paymentId;
    this.reference = reference;
    this.result = result;
    this.resultCode = resultCode;
    this.scheme = scheme;
    this.token = token;
    this.type = type;
  }

  public EckohWebhookRequestDto exception(String exception) {
    this.exception = exception;
    return this;
  }

  /**
   * Get exception
   * @return exception
   */
  
  @Schema(name = "exception", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("exception")
  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.exception = exception;
  }

  public EckohWebhookRequestDto expiry(String expiry) {
    this.expiry = expiry;
    return this;
  }

  /**
   * Get expiry
   * @return expiry
   */
  @NotNull 
  @Schema(name = "expiry", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("expiry")
  public String getExpiry() {
    return expiry;
  }

  public void setExpiry(String expiry) {
    this.expiry = expiry;
  }

  public EckohWebhookRequestDto maskedPan(String maskedPan) {
    this.maskedPan = maskedPan;
    return this;
  }

  /**
   * Get maskedPan
   * @return maskedPan
   */
  @NotNull 
  @Schema(name = "masked_pan", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("masked_pan")
  public String getMaskedPan() {
    return maskedPan;
  }

  public void setMaskedPan(String maskedPan) {
    this.maskedPan = maskedPan;
  }

  public EckohWebhookRequestDto paymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Get paymentId
   * @return paymentId
   */
  @NotNull 
  @Schema(name = "payment_id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("payment_id")
  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public EckohWebhookRequestDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Get reference
   * @return reference
   */
  @NotNull 
  @Schema(name = "reference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public EckohWebhookRequestDto result(String result) {
    this.result = result;
    return this;
  }

  /**
   * Get result
   * @return result
   */
  @NotNull 
  @Schema(name = "result", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("result")
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public EckohWebhookRequestDto resultCode(Integer resultCode) {
    this.resultCode = resultCode;
    return this;
  }

  /**
   * Get resultCode
   * @return resultCode
   */
  @NotNull 
  @Schema(name = "result_code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("result_code")
  public Integer getResultCode() {
    return resultCode;
  }

  public void setResultCode(Integer resultCode) {
    this.resultCode = resultCode;
  }

  public EckohWebhookRequestDto scheme(String scheme) {
    this.scheme = scheme;
    return this;
  }

  /**
   * Get scheme
   * @return scheme
   */
  @NotNull 
  @Schema(name = "scheme", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("scheme")
  public String getScheme() {
    return scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
  }

  public EckohWebhookRequestDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  @NotNull 
  @Schema(name = "token", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public EckohWebhookRequestDto type(TypeEnum type) {
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
    EckohWebhookRequestDto eckohWebhookRequestDto = (EckohWebhookRequestDto) o;
    return Objects.equals(this.exception, eckohWebhookRequestDto.exception) &&
        Objects.equals(this.expiry, eckohWebhookRequestDto.expiry) &&
        Objects.equals(this.maskedPan, eckohWebhookRequestDto.maskedPan) &&
        Objects.equals(this.paymentId, eckohWebhookRequestDto.paymentId) &&
        Objects.equals(this.reference, eckohWebhookRequestDto.reference) &&
        Objects.equals(this.result, eckohWebhookRequestDto.result) &&
        Objects.equals(this.resultCode, eckohWebhookRequestDto.resultCode) &&
        Objects.equals(this.scheme, eckohWebhookRequestDto.scheme) &&
        Objects.equals(this.token, eckohWebhookRequestDto.token) &&
        Objects.equals(this.type, eckohWebhookRequestDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(exception, expiry, maskedPan, paymentId, reference, result, resultCode, scheme, token, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EckohWebhookRequestDto {\n");
    sb.append("    exception: ").append(toIndentedString(exception)).append("\n");
    sb.append("    expiry: ").append(toIndentedString(expiry)).append("\n");
    sb.append("    maskedPan: ").append(toIndentedString(maskedPan)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    resultCode: ").append(toIndentedString(resultCode)).append("\n");
    sb.append("    scheme: ").append(toIndentedString(scheme)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

