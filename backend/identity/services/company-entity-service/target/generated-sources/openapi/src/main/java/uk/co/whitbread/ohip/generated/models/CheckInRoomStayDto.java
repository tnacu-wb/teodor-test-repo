package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.CheckInGuaranteeDto;
import uk.co.whitbread.ohip.generated.models.CurrentRoomInfoDto;
import uk.co.whitbread.ohip.generated.models.ExpectedTimesDto;
import uk.co.whitbread.ohip.generated.models.GuestCountsDto;
import uk.co.whitbread.ohip.generated.models.RegistrationNumberDto;
import uk.co.whitbread.ohip.generated.models.RoomRatesDto;
import uk.co.whitbread.ohip.generated.models.TotalDto;
import uk.co.whitbread.ohip.generated.models.TotalPointsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CheckInRoomStayDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInRoomStayDto {

  private @Nullable String arrivalDate;

  private @Nullable CurrentRoomInfoDto currentRoomInfo;

  private @Nullable String departureDate;

  private @Nullable ExpectedTimesDto expectedTimes;

  private @Nullable CheckInGuaranteeDto guarantee;

  private @Nullable GuestCountsDto guestCounts;

  private @Nullable Boolean printRate;

  private @Nullable RegistrationNumberDto registrationNumber;

  private @Nullable Boolean roomNumberLocked;

  @Valid
  private List<@Valid RoomRatesDto> roomRates = new ArrayList<>();

  private @Nullable TotalDto total;

  private @Nullable TotalPointsDto totalPoints;

  public CheckInRoomStayDto arrivalDate(String arrivalDate) {
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

  public CheckInRoomStayDto currentRoomInfo(CurrentRoomInfoDto currentRoomInfo) {
    this.currentRoomInfo = currentRoomInfo;
    return this;
  }

  /**
   * Get currentRoomInfo
   * @return currentRoomInfo
   */
  @Valid 
  @Schema(name = "currentRoomInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currentRoomInfo")
  public CurrentRoomInfoDto getCurrentRoomInfo() {
    return currentRoomInfo;
  }

  public void setCurrentRoomInfo(CurrentRoomInfoDto currentRoomInfo) {
    this.currentRoomInfo = currentRoomInfo;
  }

  public CheckInRoomStayDto departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
  }

  public CheckInRoomStayDto expectedTimes(ExpectedTimesDto expectedTimes) {
    this.expectedTimes = expectedTimes;
    return this;
  }

  /**
   * Get expectedTimes
   * @return expectedTimes
   */
  @Valid 
  @Schema(name = "expectedTimes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expectedTimes")
  public ExpectedTimesDto getExpectedTimes() {
    return expectedTimes;
  }

  public void setExpectedTimes(ExpectedTimesDto expectedTimes) {
    this.expectedTimes = expectedTimes;
  }

  public CheckInRoomStayDto guarantee(CheckInGuaranteeDto guarantee) {
    this.guarantee = guarantee;
    return this;
  }

  /**
   * Get guarantee
   * @return guarantee
   */
  @Valid 
  @Schema(name = "guarantee", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guarantee")
  public CheckInGuaranteeDto getGuarantee() {
    return guarantee;
  }

  public void setGuarantee(CheckInGuaranteeDto guarantee) {
    this.guarantee = guarantee;
  }

  public CheckInRoomStayDto guestCounts(GuestCountsDto guestCounts) {
    this.guestCounts = guestCounts;
    return this;
  }

  /**
   * Get guestCounts
   * @return guestCounts
   */
  @Valid 
  @Schema(name = "guestCounts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestCounts")
  public GuestCountsDto getGuestCounts() {
    return guestCounts;
  }

  public void setGuestCounts(GuestCountsDto guestCounts) {
    this.guestCounts = guestCounts;
  }

  public CheckInRoomStayDto printRate(Boolean printRate) {
    this.printRate = printRate;
    return this;
  }

  /**
   * Get printRate
   * @return printRate
   */
  
  @Schema(name = "printRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("printRate")
  public Boolean getPrintRate() {
    return printRate;
  }

  public void setPrintRate(Boolean printRate) {
    this.printRate = printRate;
  }

  public CheckInRoomStayDto registrationNumber(RegistrationNumberDto registrationNumber) {
    this.registrationNumber = registrationNumber;
    return this;
  }

  /**
   * Get registrationNumber
   * @return registrationNumber
   */
  @Valid 
  @Schema(name = "registrationNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("registrationNumber")
  public RegistrationNumberDto getRegistrationNumber() {
    return registrationNumber;
  }

  public void setRegistrationNumber(RegistrationNumberDto registrationNumber) {
    this.registrationNumber = registrationNumber;
  }

  public CheckInRoomStayDto roomNumberLocked(Boolean roomNumberLocked) {
    this.roomNumberLocked = roomNumberLocked;
    return this;
  }

  /**
   * Get roomNumberLocked
   * @return roomNumberLocked
   */
  
  @Schema(name = "roomNumberLocked", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomNumberLocked")
  public Boolean getRoomNumberLocked() {
    return roomNumberLocked;
  }

  public void setRoomNumberLocked(Boolean roomNumberLocked) {
    this.roomNumberLocked = roomNumberLocked;
  }

  public CheckInRoomStayDto roomRates(List<@Valid RoomRatesDto> roomRates) {
    this.roomRates = roomRates;
    return this;
  }

  public CheckInRoomStayDto addRoomRatesItem(RoomRatesDto roomRatesItem) {
    if (this.roomRates == null) {
      this.roomRates = new ArrayList<>();
    }
    this.roomRates.add(roomRatesItem);
    return this;
  }

  /**
   * Get roomRates
   * @return roomRates
   */
  @Valid 
  @Schema(name = "roomRates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomRates")
  public List<@Valid RoomRatesDto> getRoomRates() {
    return roomRates;
  }

  public void setRoomRates(List<@Valid RoomRatesDto> roomRates) {
    this.roomRates = roomRates;
  }

  public CheckInRoomStayDto total(TotalDto total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * @return total
   */
  @Valid 
  @Schema(name = "total", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total")
  public TotalDto getTotal() {
    return total;
  }

  public void setTotal(TotalDto total) {
    this.total = total;
  }

  public CheckInRoomStayDto totalPoints(TotalPointsDto totalPoints) {
    this.totalPoints = totalPoints;
    return this;
  }

  /**
   * Get totalPoints
   * @return totalPoints
   */
  @Valid 
  @Schema(name = "totalPoints", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalPoints")
  public TotalPointsDto getTotalPoints() {
    return totalPoints;
  }

  public void setTotalPoints(TotalPointsDto totalPoints) {
    this.totalPoints = totalPoints;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInRoomStayDto checkInRoomStayDto = (CheckInRoomStayDto) o;
    return Objects.equals(this.arrivalDate, checkInRoomStayDto.arrivalDate) &&
        Objects.equals(this.currentRoomInfo, checkInRoomStayDto.currentRoomInfo) &&
        Objects.equals(this.departureDate, checkInRoomStayDto.departureDate) &&
        Objects.equals(this.expectedTimes, checkInRoomStayDto.expectedTimes) &&
        Objects.equals(this.guarantee, checkInRoomStayDto.guarantee) &&
        Objects.equals(this.guestCounts, checkInRoomStayDto.guestCounts) &&
        Objects.equals(this.printRate, checkInRoomStayDto.printRate) &&
        Objects.equals(this.registrationNumber, checkInRoomStayDto.registrationNumber) &&
        Objects.equals(this.roomNumberLocked, checkInRoomStayDto.roomNumberLocked) &&
        Objects.equals(this.roomRates, checkInRoomStayDto.roomRates) &&
        Objects.equals(this.total, checkInRoomStayDto.total) &&
        Objects.equals(this.totalPoints, checkInRoomStayDto.totalPoints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, currentRoomInfo, departureDate, expectedTimes, guarantee, guestCounts, printRate, registrationNumber, roomNumberLocked, roomRates, total, totalPoints);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInRoomStayDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    currentRoomInfo: ").append(toIndentedString(currentRoomInfo)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    expectedTimes: ").append(toIndentedString(expectedTimes)).append("\n");
    sb.append("    guarantee: ").append(toIndentedString(guarantee)).append("\n");
    sb.append("    guestCounts: ").append(toIndentedString(guestCounts)).append("\n");
    sb.append("    printRate: ").append(toIndentedString(printRate)).append("\n");
    sb.append("    registrationNumber: ").append(toIndentedString(registrationNumber)).append("\n");
    sb.append("    roomNumberLocked: ").append(toIndentedString(roomNumberLocked)).append("\n");
    sb.append("    roomRates: ").append(toIndentedString(roomRates)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    totalPoints: ").append(toIndentedString(totalPoints)).append("\n");
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

