package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.reservation.BookingChannelDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AmendStayDatesRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmendStayDatesRequestDto {

  private BookingChannelDto bookingChannel;

  private String newEndDate;

  private String newStartDate;

  private String tempBookingRef;

  private @Nullable String token;

  public AmendStayDatesRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AmendStayDatesRequestDto(BookingChannelDto bookingChannel, String newEndDate, String newStartDate, String tempBookingRef) {
    this.bookingChannel = bookingChannel;
    this.newEndDate = newEndDate;
    this.newStartDate = newStartDate;
    this.tempBookingRef = tempBookingRef;
  }

  public AmendStayDatesRequestDto bookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
    return this;
  }

  /**
   * Get bookingChannel
   * @return bookingChannel
   */
  @NotNull @Valid 
  @Schema(name = "bookingChannel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingChannel")
  public BookingChannelDto getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public AmendStayDatesRequestDto newEndDate(String newEndDate) {
    this.newEndDate = newEndDate;
    return this;
  }

  /**
   * Get newEndDate
   * @return newEndDate
   */
  @NotNull 
  @Schema(name = "newEndDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("newEndDate")
  public String getNewEndDate() {
    return newEndDate;
  }

  public void setNewEndDate(String newEndDate) {
    this.newEndDate = newEndDate;
  }

  public AmendStayDatesRequestDto newStartDate(String newStartDate) {
    this.newStartDate = newStartDate;
    return this;
  }

  /**
   * Get newStartDate
   * @return newStartDate
   */
  @NotNull 
  @Schema(name = "newStartDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("newStartDate")
  public String getNewStartDate() {
    return newStartDate;
  }

  public void setNewStartDate(String newStartDate) {
    this.newStartDate = newStartDate;
  }

  public AmendStayDatesRequestDto tempBookingRef(String tempBookingRef) {
    this.tempBookingRef = tempBookingRef;
    return this;
  }

  /**
   * Get tempBookingRef
   * @return tempBookingRef
   */
  @NotNull 
  @Schema(name = "tempBookingRef", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tempBookingRef")
  public String getTempBookingRef() {
    return tempBookingRef;
  }

  public void setTempBookingRef(String tempBookingRef) {
    this.tempBookingRef = tempBookingRef;
  }

  public AmendStayDatesRequestDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  
  @Schema(name = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AmendStayDatesRequestDto amendStayDatesRequestDto = (AmendStayDatesRequestDto) o;
    return Objects.equals(this.bookingChannel, amendStayDatesRequestDto.bookingChannel) &&
        Objects.equals(this.newEndDate, amendStayDatesRequestDto.newEndDate) &&
        Objects.equals(this.newStartDate, amendStayDatesRequestDto.newStartDate) &&
        Objects.equals(this.tempBookingRef, amendStayDatesRequestDto.tempBookingRef) &&
        Objects.equals(this.token, amendStayDatesRequestDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, newEndDate, newStartDate, tempBookingRef, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmendStayDatesRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    newEndDate: ").append(toIndentedString(newEndDate)).append("\n");
    sb.append("    newStartDate: ").append(toIndentedString(newStartDate)).append("\n");
    sb.append("    tempBookingRef: ").append(toIndentedString(tempBookingRef)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

