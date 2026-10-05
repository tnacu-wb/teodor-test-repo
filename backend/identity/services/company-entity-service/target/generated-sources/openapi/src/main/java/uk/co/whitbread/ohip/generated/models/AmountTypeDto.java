package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.TotalTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AmountTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmountTypeDto {

  private TotalTypeDto base;

  private String end;

  private String start;

  public AmountTypeDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AmountTypeDto(TotalTypeDto base, String end, String start) {
    this.base = base;
    this.end = end;
    this.start = start;
  }

  public AmountTypeDto base(TotalTypeDto base) {
    this.base = base;
    return this;
  }

  /**
   * Get base
   * @return base
   */
  @NotNull @Valid 
  @Schema(name = "base", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("base")
  public TotalTypeDto getBase() {
    return base;
  }

  public void setBase(TotalTypeDto base) {
    this.base = base;
  }

  public AmountTypeDto end(String end) {
    this.end = end;
    return this;
  }

  /**
   * Get end
   * @return end
   */
  @NotNull 
  @Schema(name = "end", example = "2025-03-31", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("end")
  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public AmountTypeDto start(String start) {
    this.start = start;
    return this;
  }

  /**
   * Get start
   * @return start
   */
  @NotNull 
  @Schema(name = "start", example = "2025-03-31", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("start")
  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AmountTypeDto amountTypeDto = (AmountTypeDto) o;
    return Objects.equals(this.base, amountTypeDto.base) &&
        Objects.equals(this.end, amountTypeDto.end) &&
        Objects.equals(this.start, amountTypeDto.start);
  }

  @Override
  public int hashCode() {
    return Objects.hash(base, end, start);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmountTypeDto {\n");
    sb.append("    base: ").append(toIndentedString(base)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
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

