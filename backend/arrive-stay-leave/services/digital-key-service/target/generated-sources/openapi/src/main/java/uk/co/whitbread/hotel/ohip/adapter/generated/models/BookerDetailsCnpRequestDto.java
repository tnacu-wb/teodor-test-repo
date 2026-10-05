package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookerDetailsCnpDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BookerDetailsCnpRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookerDetailsCnpRequestDto {

  private @Nullable BookerDetailsCnpDto booker;

  private String hotelId;

  @Valid
  private List<String> reservationIds;

  public BookerDetailsCnpRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BookerDetailsCnpRequestDto(String hotelId, List<String> reservationIds) {
    this.hotelId = hotelId;
    this.reservationIds = reservationIds;
  }

  public BookerDetailsCnpRequestDto booker(BookerDetailsCnpDto booker) {
    this.booker = booker;
    return this;
  }

  /**
   * Get booker
   * @return booker
   */
  @Valid 
  @Schema(name = "booker", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("booker")
  public BookerDetailsCnpDto getBooker() {
    return booker;
  }

  public void setBooker(BookerDetailsCnpDto booker) {
    this.booker = booker;
  }

  public BookerDetailsCnpRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public BookerDetailsCnpRequestDto reservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
    return this;
  }

  public BookerDetailsCnpRequestDto addReservationIdsItem(String reservationIdsItem) {
    if (this.reservationIds == null) {
      this.reservationIds = new ArrayList<>();
    }
    this.reservationIds.add(reservationIdsItem);
    return this;
  }

  /**
   * Get reservationIds
   * @return reservationIds
   */
  @NotNull 
  @Schema(name = "reservationIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationIds")
  public List<String> getReservationIds() {
    return reservationIds;
  }

  public void setReservationIds(List<String> reservationIds) {
    this.reservationIds = reservationIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookerDetailsCnpRequestDto bookerDetailsCnpRequestDto = (BookerDetailsCnpRequestDto) o;
    return Objects.equals(this.booker, bookerDetailsCnpRequestDto.booker) &&
        Objects.equals(this.hotelId, bookerDetailsCnpRequestDto.hotelId) &&
        Objects.equals(this.reservationIds, bookerDetailsCnpRequestDto.reservationIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booker, hotelId, reservationIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookerDetailsCnpRequestDto {\n");
    sb.append("    booker: ").append(toIndentedString(booker)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reservationIds: ").append(toIndentedString(reservationIds)).append("\n");
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

