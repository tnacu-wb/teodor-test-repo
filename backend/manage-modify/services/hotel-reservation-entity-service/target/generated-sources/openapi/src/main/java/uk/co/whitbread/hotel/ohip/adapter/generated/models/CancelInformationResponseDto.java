package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * CancelInformationResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CancelInformationResponseDto {

  private @Nullable Boolean isCancellable;

  public CancelInformationResponseDto isCancellable(Boolean isCancellable) {
    this.isCancellable = isCancellable;
    return this;
  }

  /**
   * Get isCancellable
   * @return isCancellable
   */
  
  @Schema(name = "isCancellable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCancellable")
  public Boolean getIsCancellable() {
    return isCancellable;
  }

  public void setIsCancellable(Boolean isCancellable) {
    this.isCancellable = isCancellable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancelInformationResponseDto cancelInformationResponseDto = (CancelInformationResponseDto) o;
    return Objects.equals(this.isCancellable, cancelInformationResponseDto.isCancellable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isCancellable);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancelInformationResponseDto {\n");
    sb.append("    isCancellable: ").append(toIndentedString(isCancellable)).append("\n");
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

