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
 * CopyBookingRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CopyBookingRequestDto {

  private BookingChannelDto bookingChannel;

  private String originalBasketReference;

  private @Nullable String token;

  public CopyBookingRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CopyBookingRequestDto(BookingChannelDto bookingChannel, String originalBasketReference) {
    this.bookingChannel = bookingChannel;
    this.originalBasketReference = originalBasketReference;
  }

  public CopyBookingRequestDto bookingChannel(BookingChannelDto bookingChannel) {
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

  public CopyBookingRequestDto originalBasketReference(String originalBasketReference) {
    this.originalBasketReference = originalBasketReference;
    return this;
  }

  /**
   * Get originalBasketReference
   * @return originalBasketReference
   */
  @NotNull 
  @Schema(name = "originalBasketReference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("originalBasketReference")
  public String getOriginalBasketReference() {
    return originalBasketReference;
  }

  public void setOriginalBasketReference(String originalBasketReference) {
    this.originalBasketReference = originalBasketReference;
  }

  public CopyBookingRequestDto token(String token) {
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
    CopyBookingRequestDto copyBookingRequestDto = (CopyBookingRequestDto) o;
    return Objects.equals(this.bookingChannel, copyBookingRequestDto.bookingChannel) &&
        Objects.equals(this.originalBasketReference, copyBookingRequestDto.originalBasketReference) &&
        Objects.equals(this.token, copyBookingRequestDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, originalBasketReference, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CopyBookingRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    originalBasketReference: ").append(toIndentedString(originalBasketReference)).append("\n");
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

