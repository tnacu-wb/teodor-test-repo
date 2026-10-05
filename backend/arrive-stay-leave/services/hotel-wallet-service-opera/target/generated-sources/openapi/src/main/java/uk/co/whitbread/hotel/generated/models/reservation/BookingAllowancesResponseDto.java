package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.BookingAllowanceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BookingAllowancesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingAllowancesResponseDto {

  @Valid
  private List<@Valid BookingAllowanceDto> bookingAllowances = new ArrayList<>();

  public BookingAllowancesResponseDto bookingAllowances(List<@Valid BookingAllowanceDto> bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
    return this;
  }

  public BookingAllowancesResponseDto addBookingAllowancesItem(BookingAllowanceDto bookingAllowancesItem) {
    if (this.bookingAllowances == null) {
      this.bookingAllowances = new ArrayList<>();
    }
    this.bookingAllowances.add(bookingAllowancesItem);
    return this;
  }

  /**
   * Get bookingAllowances
   * @return bookingAllowances
   */
  @Valid 
  @Schema(name = "bookingAllowances", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingAllowances")
  public List<@Valid BookingAllowanceDto> getBookingAllowances() {
    return bookingAllowances;
  }

  public void setBookingAllowances(List<@Valid BookingAllowanceDto> bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingAllowancesResponseDto bookingAllowancesResponseDto = (BookingAllowancesResponseDto) o;
    return Objects.equals(this.bookingAllowances, bookingAllowancesResponseDto.bookingAllowances);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingAllowances);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingAllowancesResponseDto {\n");
    sb.append("    bookingAllowances: ").append(toIndentedString(bookingAllowances)).append("\n");
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

