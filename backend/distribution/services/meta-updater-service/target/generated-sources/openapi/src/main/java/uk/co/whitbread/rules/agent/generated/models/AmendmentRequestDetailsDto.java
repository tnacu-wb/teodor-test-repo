package uk.co.whitbread.rules.agent.generated.models;

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
 * AmendmentRequestDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:31.460652+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmendmentRequestDetailsDto {

  private @Nullable String arrivalDate;

  private @Nullable String hotelCountryCode;

  private @Nullable String hotelLocalDateTime;

  private @Nullable String rateType;

  public AmendmentRequestDetailsDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public AmendmentRequestDetailsDto hotelCountryCode(String hotelCountryCode) {
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

  public AmendmentRequestDetailsDto hotelLocalDateTime(String hotelLocalDateTime) {
    this.hotelLocalDateTime = hotelLocalDateTime;
    return this;
  }

  /**
   * Get hotelLocalDateTime
   * @return hotelLocalDateTime
   */
  
  @Schema(name = "hotelLocalDateTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelLocalDateTime")
  public String getHotelLocalDateTime() {
    return hotelLocalDateTime;
  }

  public void setHotelLocalDateTime(String hotelLocalDateTime) {
    this.hotelLocalDateTime = hotelLocalDateTime;
  }

  public AmendmentRequestDetailsDto rateType(String rateType) {
    this.rateType = rateType;
    return this;
  }

  /**
   * Get rateType
   * @return rateType
   */
  
  @Schema(name = "rateType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateType")
  public String getRateType() {
    return rateType;
  }

  public void setRateType(String rateType) {
    this.rateType = rateType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AmendmentRequestDetailsDto amendmentRequestDetailsDto = (AmendmentRequestDetailsDto) o;
    return Objects.equals(this.arrivalDate, amendmentRequestDetailsDto.arrivalDate) &&
        Objects.equals(this.hotelCountryCode, amendmentRequestDetailsDto.hotelCountryCode) &&
        Objects.equals(this.hotelLocalDateTime, amendmentRequestDetailsDto.hotelLocalDateTime) &&
        Objects.equals(this.rateType, amendmentRequestDetailsDto.rateType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, hotelCountryCode, hotelLocalDateTime, rateType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmendmentRequestDetailsDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    hotelCountryCode: ").append(toIndentedString(hotelCountryCode)).append("\n");
    sb.append("    hotelLocalDateTime: ").append(toIndentedString(hotelLocalDateTime)).append("\n");
    sb.append("    rateType: ").append(toIndentedString(rateType)).append("\n");
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

