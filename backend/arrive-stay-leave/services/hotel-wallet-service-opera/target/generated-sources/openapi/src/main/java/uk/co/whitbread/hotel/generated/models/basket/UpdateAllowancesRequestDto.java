package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.BookingAllowanceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateAllowancesRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateAllowancesRequestDto {

  @Valid
  private List<@Valid BookingAllowanceDto> bookingAllowances = new ArrayList<>();

  public UpdateAllowancesRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateAllowancesRequestDto(List<@Valid BookingAllowanceDto> bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
  }

  public UpdateAllowancesRequestDto bookingAllowances(List<@Valid BookingAllowanceDto> bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
    return this;
  }

  public UpdateAllowancesRequestDto addBookingAllowancesItem(BookingAllowanceDto bookingAllowancesItem) {
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
  @NotNull @Valid 
  @Schema(name = "bookingAllowances", requiredMode = Schema.RequiredMode.REQUIRED)
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
    UpdateAllowancesRequestDto updateAllowancesRequestDto = (UpdateAllowancesRequestDto) o;
    return Objects.equals(this.bookingAllowances, updateAllowancesRequestDto.bookingAllowances);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingAllowances);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateAllowancesRequestDto {\n");
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

