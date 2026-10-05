package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.BookingAllowanceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BookingAllowancesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingAllowancesResponseDto {

  @Valid
  private List<@Valid BookingAllowanceDto> bookingAllowances = new ArrayList<>();

  private @Nullable String businessNotes;

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

  public BookingAllowancesResponseDto businessNotes(String businessNotes) {
    this.businessNotes = businessNotes;
    return this;
  }

  /**
   * Get businessNotes
   * @return businessNotes
   */
  
  @Schema(name = "businessNotes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessNotes")
  public String getBusinessNotes() {
    return businessNotes;
  }

  public void setBusinessNotes(String businessNotes) {
    this.businessNotes = businessNotes;
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
    return Objects.equals(this.bookingAllowances, bookingAllowancesResponseDto.bookingAllowances) &&
        Objects.equals(this.businessNotes, bookingAllowancesResponseDto.businessNotes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingAllowances, businessNotes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingAllowancesResponseDto {\n");
    sb.append("    bookingAllowances: ").append(toIndentedString(bookingAllowances)).append("\n");
    sb.append("    businessNotes: ").append(toIndentedString(businessNotes)).append("\n");
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

