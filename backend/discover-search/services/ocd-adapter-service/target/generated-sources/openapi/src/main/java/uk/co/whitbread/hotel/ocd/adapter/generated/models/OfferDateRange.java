package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Date range information
 */

@Schema(name = "OfferDateRange", description = "Date range information")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferDateRange {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate start;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate end;

  public OfferDateRange start(LocalDate start) {
    this.start = start;
    return this;
  }

  /**
   * The first night of the stay where the charge will be applied.
   * @return start
   */
  @Valid 
  @Schema(name = "start", example = "2021-06-01", description = "The first night of the stay where the charge will be applied.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start")
  public LocalDate getStart() {
    return start;
  }

  public void setStart(LocalDate start) {
    this.start = start;
  }

  public OfferDateRange end(LocalDate end) {
    this.end = end;
    return this;
  }

  /**
   * The last night of the stay where the charge will be applied.
   * @return end
   */
  @Valid 
  @Schema(name = "end", example = "2021-06-05", description = "The last night of the stay where the charge will be applied.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end")
  public LocalDate getEnd() {
    return end;
  }

  public void setEnd(LocalDate end) {
    this.end = end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferDateRange offerDateRange = (OfferDateRange) o;
    return Objects.equals(this.start, offerDateRange.start) &&
        Objects.equals(this.end, offerDateRange.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferDateRange {\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
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

