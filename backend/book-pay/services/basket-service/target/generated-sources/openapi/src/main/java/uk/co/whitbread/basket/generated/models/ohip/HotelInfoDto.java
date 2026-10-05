package uk.co.whitbread.basket.generated.models.ohip;

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
 * HotelInfoDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelInfoDto {

  private @Nullable String checkInTime;

  private @Nullable String checkOutTime;

  private @Nullable String currencyCode;

  private @Nullable String hotelCountryCode;

  private @Nullable String hotelTimeZone;

  private @Nullable String languageCode;

  private @Nullable String threeLetterId;

  public HotelInfoDto checkInTime(String checkInTime) {
    this.checkInTime = checkInTime;
    return this;
  }

  /**
   * Get checkInTime
   * @return checkInTime
   */
  
  @Schema(name = "checkInTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkInTime")
  public String getCheckInTime() {
    return checkInTime;
  }

  public void setCheckInTime(String checkInTime) {
    this.checkInTime = checkInTime;
  }

  public HotelInfoDto checkOutTime(String checkOutTime) {
    this.checkOutTime = checkOutTime;
    return this;
  }

  /**
   * Get checkOutTime
   * @return checkOutTime
   */
  
  @Schema(name = "checkOutTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkOutTime")
  public String getCheckOutTime() {
    return checkOutTime;
  }

  public void setCheckOutTime(String checkOutTime) {
    this.checkOutTime = checkOutTime;
  }

  public HotelInfoDto currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * Get currencyCode
   * @return currencyCode
   */
  
  @Schema(name = "currencyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public HotelInfoDto hotelCountryCode(String hotelCountryCode) {
    this.hotelCountryCode = hotelCountryCode;
    return this;
  }

  /**
   * Get hotelCountryCode
   * @return hotelCountryCode
   */
  
  @Schema(name = "hotelCountryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCountryCode")
  public String getHotelCountryCode() {
    return hotelCountryCode;
  }

  public void setHotelCountryCode(String hotelCountryCode) {
    this.hotelCountryCode = hotelCountryCode;
  }

  public HotelInfoDto hotelTimeZone(String hotelTimeZone) {
    this.hotelTimeZone = hotelTimeZone;
    return this;
  }

  /**
   * Get hotelTimeZone
   * @return hotelTimeZone
   */
  
  @Schema(name = "hotelTimeZone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelTimeZone")
  public String getHotelTimeZone() {
    return hotelTimeZone;
  }

  public void setHotelTimeZone(String hotelTimeZone) {
    this.hotelTimeZone = hotelTimeZone;
  }

  public HotelInfoDto languageCode(String languageCode) {
    this.languageCode = languageCode;
    return this;
  }

  /**
   * Get languageCode
   * @return languageCode
   */
  
  @Schema(name = "languageCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("languageCode")
  public String getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }

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
    return Objects.equals(this.checkInTime, hotelInfoDto.checkInTime) &&
        Objects.equals(this.checkOutTime, hotelInfoDto.checkOutTime) &&
        Objects.equals(this.currencyCode, hotelInfoDto.currencyCode) &&
        Objects.equals(this.hotelCountryCode, hotelInfoDto.hotelCountryCode) &&
        Objects.equals(this.hotelTimeZone, hotelInfoDto.hotelTimeZone) &&
        Objects.equals(this.languageCode, hotelInfoDto.languageCode) &&
        Objects.equals(this.threeLetterId, hotelInfoDto.threeLetterId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(checkInTime, checkOutTime, currencyCode, hotelCountryCode, hotelTimeZone, languageCode, threeLetterId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelInfoDto {\n");
    sb.append("    checkInTime: ").append(toIndentedString(checkInTime)).append("\n");
    sb.append("    checkOutTime: ").append(toIndentedString(checkOutTime)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    hotelCountryCode: ").append(toIndentedString(hotelCountryCode)).append("\n");
    sb.append("    hotelTimeZone: ").append(toIndentedString(hotelTimeZone)).append("\n");
    sb.append("    languageCode: ").append(toIndentedString(languageCode)).append("\n");
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

