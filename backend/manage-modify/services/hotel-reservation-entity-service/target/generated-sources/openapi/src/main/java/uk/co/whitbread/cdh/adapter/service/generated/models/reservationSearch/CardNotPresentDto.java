package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

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
 * CardNotPresentDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.868523+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardNotPresentDto {

  private @Nullable String businessAccountPassword;

  private @Nullable String businessAccountUsername;

  public CardNotPresentDto businessAccountPassword(String businessAccountPassword) {
    this.businessAccountPassword = businessAccountPassword;
    return this;
  }

  /**
   * Get businessAccountPassword
   * @return businessAccountPassword
   */
  
  @Schema(name = "businessAccountPassword", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessAccountPassword")
  public String getBusinessAccountPassword() {
    return businessAccountPassword;
  }

  public void setBusinessAccountPassword(String businessAccountPassword) {
    this.businessAccountPassword = businessAccountPassword;
  }

  public CardNotPresentDto businessAccountUsername(String businessAccountUsername) {
    this.businessAccountUsername = businessAccountUsername;
    return this;
  }

  /**
   * Get businessAccountUsername
   * @return businessAccountUsername
   */
  
  @Schema(name = "businessAccountUsername", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessAccountUsername")
  public String getBusinessAccountUsername() {
    return businessAccountUsername;
  }

  public void setBusinessAccountUsername(String businessAccountUsername) {
    this.businessAccountUsername = businessAccountUsername;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardNotPresentDto cardNotPresentDto = (CardNotPresentDto) o;
    return Objects.equals(this.businessAccountPassword, cardNotPresentDto.businessAccountPassword) &&
        Objects.equals(this.businessAccountUsername, cardNotPresentDto.businessAccountUsername);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessAccountPassword, businessAccountUsername);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardNotPresentDto {\n");
    sb.append("    businessAccountPassword: ").append(toIndentedString(businessAccountPassword)).append("\n");
    sb.append("    businessAccountUsername: ").append(toIndentedString(businessAccountUsername)).append("\n");
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

