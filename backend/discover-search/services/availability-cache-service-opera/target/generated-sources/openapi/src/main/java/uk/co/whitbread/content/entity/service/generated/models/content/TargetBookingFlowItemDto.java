package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TargetBookingFlowItemDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class TargetBookingFlowItemDto {

  private @Nullable String bookingFlowPath;

  private @Nullable String id;

  private @Nullable String rateCode;

  public TargetBookingFlowItemDto bookingFlowPath(String bookingFlowPath) {
    this.bookingFlowPath = bookingFlowPath;
    return this;
  }

  /**
   * Get bookingFlowPath
   * @return bookingFlowPath
   */
  
  @Schema(name = "bookingFlowPath", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingFlowPath")
  public String getBookingFlowPath() {
    return bookingFlowPath;
  }

  public void setBookingFlowPath(String bookingFlowPath) {
    this.bookingFlowPath = bookingFlowPath;
  }

  public TargetBookingFlowItemDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TargetBookingFlowItemDto rateCode(String rateCode) {
    this.rateCode = rateCode;
    return this;
  }

  /**
   * Get rateCode
   * @return rateCode
   */
  
  @Schema(name = "rateCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCode")
  public String getRateCode() {
    return rateCode;
  }

  public void setRateCode(String rateCode) {
    this.rateCode = rateCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TargetBookingFlowItemDto targetBookingFlowItemDto = (TargetBookingFlowItemDto) o;
    return Objects.equals(this.bookingFlowPath, targetBookingFlowItemDto.bookingFlowPath) &&
        Objects.equals(this.id, targetBookingFlowItemDto.id) &&
        Objects.equals(this.rateCode, targetBookingFlowItemDto.rateCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingFlowPath, id, rateCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TargetBookingFlowItemDto {\n");
    sb.append("    bookingFlowPath: ").append(toIndentedString(bookingFlowPath)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    rateCode: ").append(toIndentedString(rateCode)).append("\n");
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

