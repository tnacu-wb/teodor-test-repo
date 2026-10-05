package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.card.generated.models.payments.EckohExceptionDto;
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
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EckohResponseDto {

  private @Nullable String content;

  private @Nullable EckohExceptionDto exception;

  private @Nullable String expiry;

  private @Nullable String maskedPan;

  private @Nullable String reference;

  private @Nullable String result;

  private @Nullable Long resultCode;

  private @Nullable String scheme;

  private @Nullable String token;

  private @Nullable String type;

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

  public EckohResponseDto exception(EckohExceptionDto exception) {
    this.exception = exception;
    return this;
  }

  /**
   * Get exception
   * @return exception
   */
  @Valid 
  @Schema(name = "exception", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("exception")
  public EckohExceptionDto getException() {
    return exception;
  }

  public void setException(EckohExceptionDto exception) {
    this.exception = exception;
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

  public EckohResponseDto resultCode(Long resultCode) {
    this.resultCode = resultCode;
    return this;
  }

  /**
   * Get resultCode
   * @return resultCode
   */
  
  @Schema(name = "resultCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resultCode")
  public Long getResultCode() {
    return resultCode;
  }

  public void setResultCode(Long resultCode) {
    this.resultCode = resultCode;
  }

  public EckohResponseDto scheme(String scheme) {
    this.scheme = scheme;
    return this;
  }

  /**
   * Get scheme
   * @return scheme
   */
  
  @Schema(name = "scheme", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scheme")
  public String getScheme() {
    return scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
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
        Objects.equals(this.exception, eckohResponse.exception) &&
        Objects.equals(this.expiry, eckohResponse.expiry) &&
        Objects.equals(this.maskedPan, eckohResponse.maskedPan) &&
        Objects.equals(this.reference, eckohResponse.reference) &&
        Objects.equals(this.result, eckohResponse.result) &&
        Objects.equals(this.resultCode, eckohResponse.resultCode) &&
        Objects.equals(this.scheme, eckohResponse.scheme) &&
        Objects.equals(this.token, eckohResponse.token) &&
        Objects.equals(this.type, eckohResponse.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, exception, expiry, maskedPan, reference, result, resultCode, scheme, token, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EckohResponseDto {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    exception: ").append(toIndentedString(exception)).append("\n");
    sb.append("    expiry: ").append(toIndentedString(expiry)).append("\n");
    sb.append("    maskedPan: ").append(toIndentedString(maskedPan)).append("\n");
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

