package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancellationReasonDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CancellationReasonsResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CancellationReasonsResponseDto {

  @Valid
  private List<@Valid CancellationReasonDto> cancellationReasons = new ArrayList<>();

  public CancellationReasonsResponseDto cancellationReasons(List<@Valid CancellationReasonDto> cancellationReasons) {
    this.cancellationReasons = cancellationReasons;
    return this;
  }

  public CancellationReasonsResponseDto addCancellationReasonsItem(CancellationReasonDto cancellationReasonsItem) {
    if (this.cancellationReasons == null) {
      this.cancellationReasons = new ArrayList<>();
    }
    this.cancellationReasons.add(cancellationReasonsItem);
    return this;
  }

  /**
   * Get cancellationReasons
   * @return cancellationReasons
   */
  @Valid 
  @Schema(name = "cancellationReasons", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationReasons")
  public List<@Valid CancellationReasonDto> getCancellationReasons() {
    return cancellationReasons;
  }

  public void setCancellationReasons(List<@Valid CancellationReasonDto> cancellationReasons) {
    this.cancellationReasons = cancellationReasons;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancellationReasonsResponseDto cancellationReasonsResponseDto = (CancellationReasonsResponseDto) o;
    return Objects.equals(this.cancellationReasons, cancellationReasonsResponseDto.cancellationReasons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cancellationReasons);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancellationReasonsResponseDto {\n");
    sb.append("    cancellationReasons: ").append(toIndentedString(cancellationReasons)).append("\n");
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

