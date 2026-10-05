package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

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
 * UserInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UserInfoDto {

  private @Nullable String idContext;

  private @Nullable String userId;

  public UserInfoDto idContext(String idContext) {
    this.idContext = idContext;
    return this;
  }

  /**
   * Get idContext
   * @return idContext
   */
  
  @Schema(name = "idContext", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idContext")
  public String getIdContext() {
    return idContext;
  }

  public void setIdContext(String idContext) {
    this.idContext = idContext;
  }

  public UserInfoDto userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  
  @Schema(name = "userId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserInfoDto userInfoDto = (UserInfoDto) o;
    return Objects.equals(this.idContext, userInfoDto.idContext) &&
        Objects.equals(this.userId, userInfoDto.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(idContext, userId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserInfoDto {\n");
    sb.append("    idContext: ").append(toIndentedString(idContext)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

