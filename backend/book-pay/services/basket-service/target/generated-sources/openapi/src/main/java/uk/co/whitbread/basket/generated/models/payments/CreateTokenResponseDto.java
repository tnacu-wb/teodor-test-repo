package uk.co.whitbread.basket.generated.models.payments;

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
 * CreateTokenResponseDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@JsonTypeName("CreateTokenResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:02.841275+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CreateTokenResponseDto {

  private String token;

  public CreateTokenResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateTokenResponseDto(String token) {
    this.token = token;
  }

  public CreateTokenResponseDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Created token
   * @return token
   */
  @NotNull 
  @Schema(name = "token", description = "Created token", requiredMode = Schema.RequiredMode.REQUIRED)
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
    CreateTokenResponseDto createTokenResponse = (CreateTokenResponseDto) o;
    return Objects.equals(this.token, createTokenResponse.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateTokenResponseDto {\n");
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

