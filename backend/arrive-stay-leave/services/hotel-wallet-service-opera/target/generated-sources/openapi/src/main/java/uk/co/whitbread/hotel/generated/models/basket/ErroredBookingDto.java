package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.BasketError;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ErroredBookingDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ErroredBookingDto {

  private @Nullable BasketError basketError;

  private Boolean isErroredBooking;

  public ErroredBookingDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ErroredBookingDto(Boolean isErroredBooking) {
    this.isErroredBooking = isErroredBooking;
  }

  public ErroredBookingDto basketError(BasketError basketError) {
    this.basketError = basketError;
    return this;
  }

  /**
   * Get basketError
   * @return basketError
   */
  @Valid 
  @Schema(name = "basketError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketError")
  public BasketError getBasketError() {
    return basketError;
  }

  public void setBasketError(BasketError basketError) {
    this.basketError = basketError;
  }

  public ErroredBookingDto isErroredBooking(Boolean isErroredBooking) {
    this.isErroredBooking = isErroredBooking;
    return this;
  }

  /**
   * Get isErroredBooking
   * @return isErroredBooking
   */
  @NotNull 
  @Schema(name = "isErroredBooking", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isErroredBooking")
  public Boolean getIsErroredBooking() {
    return isErroredBooking;
  }

  public void setIsErroredBooking(Boolean isErroredBooking) {
    this.isErroredBooking = isErroredBooking;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErroredBookingDto erroredBookingDto = (ErroredBookingDto) o;
    return Objects.equals(this.basketError, erroredBookingDto.basketError) &&
        Objects.equals(this.isErroredBooking, erroredBookingDto.isErroredBooking);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketError, isErroredBooking);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErroredBookingDto {\n");
    sb.append("    basketError: ").append(toIndentedString(basketError)).append("\n");
    sb.append("    isErroredBooking: ").append(toIndentedString(isErroredBooking)).append("\n");
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

