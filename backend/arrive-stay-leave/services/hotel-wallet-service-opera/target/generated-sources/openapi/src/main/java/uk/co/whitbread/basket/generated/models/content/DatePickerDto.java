package uk.co.whitbread.basket.generated.models.content;

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
 * DatePickerDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DatePickerDto {

  private @Nullable String checkOut;

  private @Nullable String invalidDate;

  private @Nullable String reset;

  public DatePickerDto checkOut(String checkOut) {
    this.checkOut = checkOut;
    return this;
  }

  /**
   * Get checkOut
   * @return checkOut
   */
  
  @Schema(name = "checkOut", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkOut")
  public String getCheckOut() {
    return checkOut;
  }

  public void setCheckOut(String checkOut) {
    this.checkOut = checkOut;
  }

  public DatePickerDto invalidDate(String invalidDate) {
    this.invalidDate = invalidDate;
    return this;
  }

  /**
   * Get invalidDate
   * @return invalidDate
   */
  
  @Schema(name = "invalidDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invalidDate")
  public String getInvalidDate() {
    return invalidDate;
  }

  public void setInvalidDate(String invalidDate) {
    this.invalidDate = invalidDate;
  }

  public DatePickerDto reset(String reset) {
    this.reset = reset;
    return this;
  }

  /**
   * Get reset
   * @return reset
   */
  
  @Schema(name = "reset", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reset")
  public String getReset() {
    return reset;
  }

  public void setReset(String reset) {
    this.reset = reset;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DatePickerDto datePickerDto = (DatePickerDto) o;
    return Objects.equals(this.checkOut, datePickerDto.checkOut) &&
        Objects.equals(this.invalidDate, datePickerDto.invalidDate) &&
        Objects.equals(this.reset, datePickerDto.reset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(checkOut, invalidDate, reset);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DatePickerDto {\n");
    sb.append("    checkOut: ").append(toIndentedString(checkOut)).append("\n");
    sb.append("    invalidDate: ").append(toIndentedString(invalidDate)).append("\n");
    sb.append("    reset: ").append(toIndentedString(reset)).append("\n");
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

