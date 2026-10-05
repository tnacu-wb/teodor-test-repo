package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EckohResponseDto
 */

@JsonTypeName("EckohResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EckohResponseDto {

  private @Nullable String content;

  private @Nullable String result;

  private @Nullable Integer resultCode;

  private @Nullable String paymentId;

  private @Nullable String maskedPan;

  private @Nullable String expiry;

  private @Nullable String type;

  private @Nullable String reference;

  private @Nullable String token;

  public EckohResponseDto content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
   */
  
  @Schema(name = "content", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public EckohResponseDto result(String result) {
    this.result = result;
    return this;
  }

  /**
   * Get result
   * @return result
   */
  
  @Schema(name = "result", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("result")
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public EckohResponseDto resultCode(Integer resultCode) {
    this.resultCode = resultCode;
    return this;
  }

  /**
   * Get resultCode
   * @return resultCode
   */
  
  @Schema(name = "resultCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resultCode")
  public Integer getResultCode() {
    return resultCode;
  }

  public void setResultCode(Integer resultCode) {
    this.resultCode = resultCode;
  }

  public EckohResponseDto paymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Get paymentId
   * @return paymentId
   */
  
  @Schema(name = "paymentId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentId")
  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public EckohResponseDto maskedPan(String maskedPan) {
    this.maskedPan = maskedPan;
    return this;
  }

  /**
   * Get maskedPan
   * @return maskedPan
   */
  
  @Schema(name = "maskedPan", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maskedPan")
  public String getMaskedPan() {
    return maskedPan;
  }

  public void setMaskedPan(String maskedPan) {
    this.maskedPan = maskedPan;
  }

  public EckohResponseDto expiry(String expiry) {
    this.expiry = expiry;
    return this;
  }

  /**
   * Get expiry
   * @return expiry
   */
  
  @Schema(name = "expiry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiry")
  public String getExpiry() {
    return expiry;
  }

  public void setExpiry(String expiry) {
    this.expiry = expiry;
  }

  public EckohResponseDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public EckohResponseDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Get reference
   * @return reference
   */
  
  @Schema(name = "reference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public EckohResponseDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  
  @Schema(name = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EckohResponseDto eckohResponse = (EckohResponseDto) o;
    return Objects.equals(this.content, eckohResponse.content) &&
        Objects.equals(this.result, eckohResponse.result) &&
        Objects.equals(this.resultCode, eckohResponse.resultCode) &&
        Objects.equals(this.paymentId, eckohResponse.paymentId) &&
        Objects.equals(this.maskedPan, eckohResponse.maskedPan) &&
        Objects.equals(this.expiry, eckohResponse.expiry) &&
        Objects.equals(this.type, eckohResponse.type) &&
        Objects.equals(this.reference, eckohResponse.reference) &&
        Objects.equals(this.token, eckohResponse.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, result, resultCode, paymentId, maskedPan, expiry, type, reference, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EckohResponseDto {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
    sb.append("    resultCode: ").append(toIndentedString(resultCode)).append("\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    maskedPan: ").append(toIndentedString(maskedPan)).append("\n");
    sb.append("    expiry: ").append(toIndentedString(expiry)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

