package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.BookingDto;
import uk.co.whitbread.hotel.generated.models.basket.RefundDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RefundRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RefundRequestDto {

  private BookingDto booking;

  private String hotelCode;

  private RefundDto refund;

  /**
   * Gets or Sets refundType
   */
  public enum RefundTypeEnum {
    FULL("FULL"),
    
    PARTIAL("PARTIAL");

    private String value;

    RefundTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static RefundTypeEnum fromValue(String value) {
      for (RefundTypeEnum b : RefundTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private RefundTypeEnum refundType;

  public RefundRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RefundRequestDto(BookingDto booking, String hotelCode, RefundDto refund, RefundTypeEnum refundType) {
    this.booking = booking;
    this.hotelCode = hotelCode;
    this.refund = refund;
    this.refundType = refundType;
  }

  public RefundRequestDto booking(BookingDto booking) {
    this.booking = booking;
    return this;
  }

  /**
   * Get booking
   * @return booking
   */
  @NotNull @Valid 
  @Schema(name = "booking", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("booking")
  public BookingDto getBooking() {
    return booking;
  }

  public void setBooking(BookingDto booking) {
    this.booking = booking;
  }

  public RefundRequestDto hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  @NotNull 
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public RefundRequestDto refund(RefundDto refund) {
    this.refund = refund;
    return this;
  }

  /**
   * Get refund
   * @return refund
   */
  @NotNull @Valid 
  @Schema(name = "refund", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("refund")
  public RefundDto getRefund() {
    return refund;
  }

  public void setRefund(RefundDto refund) {
    this.refund = refund;
  }

  public RefundRequestDto refundType(RefundTypeEnum refundType) {
    this.refundType = refundType;
    return this;
  }

  /**
   * Get refundType
   * @return refundType
   */
  @NotNull 
  @Schema(name = "refundType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("refundType")
  public RefundTypeEnum getRefundType() {
    return refundType;
  }

  public void setRefundType(RefundTypeEnum refundType) {
    this.refundType = refundType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RefundRequestDto refundRequestDto = (RefundRequestDto) o;
    return Objects.equals(this.booking, refundRequestDto.booking) &&
        Objects.equals(this.hotelCode, refundRequestDto.hotelCode) &&
        Objects.equals(this.refund, refundRequestDto.refund) &&
        Objects.equals(this.refundType, refundRequestDto.refundType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booking, hotelCode, refund, refundType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RefundRequestDto {\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    refund: ").append(toIndentedString(refund)).append("\n");
    sb.append("    refundType: ").append(toIndentedString(refundType)).append("\n");
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

