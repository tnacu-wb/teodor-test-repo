package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.BaseDto;
import uk.co.whitbread.ohip.generated.models.TotalDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CheckInRateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInRateDto {

  private @Nullable BaseDto base;

  private @Nullable String end;

  private @Nullable String shareDistributionInstruction;

  private @Nullable String start;

  private @Nullable TotalDto total;

  public CheckInRateDto base(BaseDto base) {
    this.base = base;
    return this;
  }

  /**
   * Get base
   * @return base
   */
  @Valid 
  @Schema(name = "base", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("base")
  public BaseDto getBase() {
    return base;
  }

  public void setBase(BaseDto base) {
    this.base = base;
  }

  public CheckInRateDto end(String end) {
    this.end = end;
    return this;
  }

  /**
   * Get end
   * @return end
   */
  
  @Schema(name = "end", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end")
  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public CheckInRateDto shareDistributionInstruction(String shareDistributionInstruction) {
    this.shareDistributionInstruction = shareDistributionInstruction;
    return this;
  }

  /**
   * Get shareDistributionInstruction
   * @return shareDistributionInstruction
   */
  
  @Schema(name = "shareDistributionInstruction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shareDistributionInstruction")
  public String getShareDistributionInstruction() {
    return shareDistributionInstruction;
  }

  public void setShareDistributionInstruction(String shareDistributionInstruction) {
    this.shareDistributionInstruction = shareDistributionInstruction;
  }

  public CheckInRateDto start(String start) {
    this.start = start;
    return this;
  }

  /**
   * Get start
   * @return start
   */
  
  @Schema(name = "start", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start")
  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public CheckInRateDto total(TotalDto total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * @return total
   */
  @Valid 
  @Schema(name = "total", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total")
  public TotalDto getTotal() {
    return total;
  }

  public void setTotal(TotalDto total) {
    this.total = total;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInRateDto checkInRateDto = (CheckInRateDto) o;
    return Objects.equals(this.base, checkInRateDto.base) &&
        Objects.equals(this.end, checkInRateDto.end) &&
        Objects.equals(this.shareDistributionInstruction, checkInRateDto.shareDistributionInstruction) &&
        Objects.equals(this.start, checkInRateDto.start) &&
        Objects.equals(this.total, checkInRateDto.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(base, end, shareDistributionInstruction, start, total);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInRateDto {\n");
    sb.append("    base: ").append(toIndentedString(base)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("    shareDistributionInstruction: ").append(toIndentedString(shareDistributionInstruction)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
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

