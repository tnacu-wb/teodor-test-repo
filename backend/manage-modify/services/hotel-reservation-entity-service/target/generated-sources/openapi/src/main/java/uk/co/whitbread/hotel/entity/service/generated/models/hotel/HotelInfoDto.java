package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

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
 * HotelInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelInfoDto {

  private @Nullable String threeLetterId;

  public HotelInfoDto threeLetterId(String threeLetterId) {
    this.threeLetterId = threeLetterId;
    return this;
  }

  /**
   * Get threeLetterId
   * @return threeLetterId
   */
  
  @Schema(name = "threeLetterId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("threeLetterId")
  public String getThreeLetterId() {
    return threeLetterId;
  }

  public void setThreeLetterId(String threeLetterId) {
    this.threeLetterId = threeLetterId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelInfoDto hotelInfoDto = (HotelInfoDto) o;
    return Objects.equals(this.threeLetterId, hotelInfoDto.threeLetterId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(threeLetterId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelInfoDto {\n");
    sb.append("    threeLetterId: ").append(toIndentedString(threeLetterId)).append("\n");
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

