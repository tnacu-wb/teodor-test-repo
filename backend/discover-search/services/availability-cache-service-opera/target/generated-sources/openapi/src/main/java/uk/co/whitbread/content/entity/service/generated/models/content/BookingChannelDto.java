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
 * BookingChannelDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingChannelDto {

  private @Nullable String business;

  private @Nullable String leisure;

  public BookingChannelDto business(String business) {
    this.business = business;
    return this;
  }

  /**
   * Get business
   * @return business
   */
  
  @Schema(name = "business", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public String getBusiness() {
    return business;
  }

  public void setBusiness(String business) {
    this.business = business;
  }

  public BookingChannelDto leisure(String leisure) {
    this.leisure = leisure;
    return this;
  }

  /**
   * Get leisure
   * @return leisure
   */
  
  @Schema(name = "leisure", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leisure")
  public String getLeisure() {
    return leisure;
  }

  public void setLeisure(String leisure) {
    this.leisure = leisure;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingChannelDto bookingChannelDto = (BookingChannelDto) o;
    return Objects.equals(this.business, bookingChannelDto.business) &&
        Objects.equals(this.leisure, bookingChannelDto.leisure);
  }

  @Override
  public int hashCode() {
    return Objects.hash(business, leisure);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingChannelDto {\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    leisure: ").append(toIndentedString(leisure)).append("\n");
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

