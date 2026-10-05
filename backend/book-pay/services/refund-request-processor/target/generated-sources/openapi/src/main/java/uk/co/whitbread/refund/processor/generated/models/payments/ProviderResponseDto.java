package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.refund.processor.generated.models.payments.EckohResponseDto;
import uk.co.whitbread.refund.processor.generated.models.payments.ThreeCResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProviderResponseDto
 */

@JsonTypeName("ProviderResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ProviderResponseDto {

  private @Nullable ThreeCResponseDto threecResponse;

  private @Nullable EckohResponseDto eckohResponse;

  public ProviderResponseDto threecResponse(ThreeCResponseDto threecResponse) {
    this.threecResponse = threecResponse;
    return this;
  }

  /**
   * Get threecResponse
   * @return threecResponse
   */
  @Valid 
  @Schema(name = "threecResponse", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("threecResponse")
  public ThreeCResponseDto getThreecResponse() {
    return threecResponse;
  }

  public void setThreecResponse(ThreeCResponseDto threecResponse) {
    this.threecResponse = threecResponse;
  }

  public ProviderResponseDto eckohResponse(EckohResponseDto eckohResponse) {
    this.eckohResponse = eckohResponse;
    return this;
  }

  /**
   * Get eckohResponse
   * @return eckohResponse
   */
  @Valid 
  @Schema(name = "eckohResponse", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("eckohResponse")
  public EckohResponseDto getEckohResponse() {
    return eckohResponse;
  }

  public void setEckohResponse(EckohResponseDto eckohResponse) {
    this.eckohResponse = eckohResponse;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProviderResponseDto providerResponse = (ProviderResponseDto) o;
    return Objects.equals(this.threecResponse, providerResponse.threecResponse) &&
        Objects.equals(this.eckohResponse, providerResponse.eckohResponse);
  }

  @Override
  public int hashCode() {
    return Objects.hash(threecResponse, eckohResponse);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProviderResponseDto {\n");
    sb.append("    threecResponse: ").append(toIndentedString(threecResponse)).append("\n");
    sb.append("    eckohResponse: ").append(toIndentedString(eckohResponse)).append("\n");
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

