package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackagesDetailsResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * LightweightReservationByIdDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LightweightReservationByIdDto {

  private @Nullable String checkInTime;

  private @Nullable String checkOutTime;

  private @Nullable String email;

  private @Nullable String hotelId;

  private @Nullable String purposeOfStay;

  private @Nullable String reservationId;

  @Valid
  private List<@Valid ReservationPackagesDetailsResponseDto> reservationPackageList = new ArrayList<>();

  public LightweightReservationByIdDto checkInTime(String checkInTime) {
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

  public LightweightReservationByIdDto checkOutTime(String checkOutTime) {
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

  public LightweightReservationByIdDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  
  @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LightweightReservationByIdDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public LightweightReservationByIdDto purposeOfStay(String purposeOfStay) {
    this.purposeOfStay = purposeOfStay;
    return this;
  }

  /**
   * Get purposeOfStay
   * @return purposeOfStay
   */
  
  @Schema(name = "purposeOfStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purposeOfStay")
  public String getPurposeOfStay() {
    return purposeOfStay;
  }

  public void setPurposeOfStay(String purposeOfStay) {
    this.purposeOfStay = purposeOfStay;
  }

  public LightweightReservationByIdDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public LightweightReservationByIdDto reservationPackageList(List<@Valid ReservationPackagesDetailsResponseDto> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
    return this;
  }

  public LightweightReservationByIdDto addReservationPackageListItem(ReservationPackagesDetailsResponseDto reservationPackageListItem) {
    if (this.reservationPackageList == null) {
      this.reservationPackageList = new ArrayList<>();
    }
    this.reservationPackageList.add(reservationPackageListItem);
    return this;
  }

  /**
   * Get reservationPackageList
   * @return reservationPackageList
   */
  @Valid 
  @Schema(name = "reservationPackageList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationPackageList")
  public List<@Valid ReservationPackagesDetailsResponseDto> getReservationPackageList() {
    return reservationPackageList;
  }

  public void setReservationPackageList(List<@Valid ReservationPackagesDetailsResponseDto> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LightweightReservationByIdDto lightweightReservationByIdDto = (LightweightReservationByIdDto) o;
    return Objects.equals(this.checkInTime, lightweightReservationByIdDto.checkInTime) &&
        Objects.equals(this.checkOutTime, lightweightReservationByIdDto.checkOutTime) &&
        Objects.equals(this.email, lightweightReservationByIdDto.email) &&
        Objects.equals(this.hotelId, lightweightReservationByIdDto.hotelId) &&
        Objects.equals(this.purposeOfStay, lightweightReservationByIdDto.purposeOfStay) &&
        Objects.equals(this.reservationId, lightweightReservationByIdDto.reservationId) &&
        Objects.equals(this.reservationPackageList, lightweightReservationByIdDto.reservationPackageList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(checkInTime, checkOutTime, email, hotelId, purposeOfStay, reservationId, reservationPackageList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LightweightReservationByIdDto {\n");
    sb.append("    checkInTime: ").append(toIndentedString(checkInTime)).append("\n");
    sb.append("    checkOutTime: ").append(toIndentedString(checkOutTime)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    purposeOfStay: ").append(toIndentedString(purposeOfStay)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    reservationPackageList: ").append(toIndentedString(reservationPackageList)).append("\n");
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

