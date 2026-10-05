package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CardHolderOptionsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardHolderOptionsDto {

  private @Nullable String registered;

  private @Nullable String resendCode;

  public CardHolderOptionsDto registered(String registered) {
    this.registered = registered;
    return this;
  }

  /**
   * Get registered
   * @return registered
   */
  
  @Schema(name = "registered", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("registered")
  public String getRegistered() {
    return registered;
  }

  public void setRegistered(String registered) {
    this.registered = registered;
  }

  public CardHolderOptionsDto resendCode(String resendCode) {
    this.resendCode = resendCode;
    return this;
  }

  /**
   * Get resendCode
   * @return resendCode
   */
  
  @Schema(name = "resendCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resendCode")
  public String getResendCode() {
    return resendCode;
  }

  public void setResendCode(String resendCode) {
    this.resendCode = resendCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardHolderOptionsDto cardHolderOptionsDto = (CardHolderOptionsDto) o;
    return Objects.equals(this.registered, cardHolderOptionsDto.registered) &&
        Objects.equals(this.resendCode, cardHolderOptionsDto.resendCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registered, resendCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardHolderOptionsDto {\n");
    sb.append("    registered: ").append(toIndentedString(registered)).append("\n");
    sb.append("    resendCode: ").append(toIndentedString(resendCode)).append("\n");
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

