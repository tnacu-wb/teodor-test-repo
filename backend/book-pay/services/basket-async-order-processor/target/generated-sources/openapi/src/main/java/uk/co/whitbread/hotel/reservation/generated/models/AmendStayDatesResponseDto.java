package uk.co.whitbread.hotel.reservation.generated.models;

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
 * AmendStayDatesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmendStayDatesResponseDto {

  private @Nullable String tempBasket;

  public AmendStayDatesResponseDto tempBasket(String tempBasket) {
    this.tempBasket = tempBasket;
    return this;
  }

  /**
   * Get tempBasket
   * @return tempBasket
   */
  
  @Schema(name = "tempBasket", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tempBasket")
  public String getTempBasket() {
    return tempBasket;
  }

  public void setTempBasket(String tempBasket) {
    this.tempBasket = tempBasket;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AmendStayDatesResponseDto amendStayDatesResponseDto = (AmendStayDatesResponseDto) o;
    return Objects.equals(this.tempBasket, amendStayDatesResponseDto.tempBasket);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tempBasket);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmendStayDatesResponseDto {\n");
    sb.append("    tempBasket: ").append(toIndentedString(tempBasket)).append("\n");
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

