package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomRateDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomRateDto {

  private @Nullable String cellCode;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate endDate;

  private String pmsRoomType;

  private @Nullable String rateDisplaySet;

  private String ratePlanCode;

  @Valid
  private List<String> specialRequests = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate startDate;

  public RoomRateDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomRateDto(LocalDate endDate, String pmsRoomType, String ratePlanCode, LocalDate startDate) {
    this.endDate = endDate;
    this.pmsRoomType = pmsRoomType;
    this.ratePlanCode = ratePlanCode;
    this.startDate = startDate;
  }

  public RoomRateDto cellCode(String cellCode) {
    this.cellCode = cellCode;
    return this;
  }

  /**
   * Get cellCode
   * @return cellCode
   */
  
  @Schema(name = "cellCode", example = "ABC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCode")
  public String getCellCode() {
    return cellCode;
  }

  public void setCellCode(String cellCode) {
    this.cellCode = cellCode;
  }

  public RoomRateDto endDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
   */
  @NotNull @Valid 
  @Schema(name = "endDate", example = "2015-10-21", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("endDate")
  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public RoomRateDto pmsRoomType(String pmsRoomType) {
    this.pmsRoomType = pmsRoomType;
    return this;
  }

  /**
   * Get pmsRoomType
   * @return pmsRoomType
   */
  @NotNull 
  @Schema(name = "pmsRoomType", example = "FMTRPL", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("pmsRoomType")
  public String getPmsRoomType() {
    return pmsRoomType;
  }

  public void setPmsRoomType(String pmsRoomType) {
    this.pmsRoomType = pmsRoomType;
  }

  public RoomRateDto rateDisplaySet(String rateDisplaySet) {
    this.rateDisplaySet = rateDisplaySet;
    return this;
  }

  /**
   * Get rateDisplaySet
   * @return rateDisplaySet
   */
  
  @Schema(name = "rateDisplaySet", example = "NEG", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateDisplaySet")
  public String getRateDisplaySet() {
    return rateDisplaySet;
  }

  public void setRateDisplaySet(String rateDisplaySet) {
    this.rateDisplaySet = rateDisplaySet;
  }

  public RoomRateDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  @NotNull 
  @Schema(name = "ratePlanCode", example = "FLEXRATE", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RoomRateDto specialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public RoomRateDto addSpecialRequestsItem(String specialRequestsItem) {
    if (this.specialRequests == null) {
      this.specialRequests = new ArrayList<>();
    }
    this.specialRequests.add(specialRequestsItem);
    return this;
  }

  /**
   * Get specialRequests
   * @return specialRequests
   */
  
  @Schema(name = "specialRequests", example = "SNGL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specialRequests")
  public List<String> getSpecialRequests() {
    return specialRequests;
  }

  public void setSpecialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
  }

  public RoomRateDto startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  @NotNull @Valid 
  @Schema(name = "startDate", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("startDate")
  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomRateDto roomRateDto = (RoomRateDto) o;
    return Objects.equals(this.cellCode, roomRateDto.cellCode) &&
        Objects.equals(this.endDate, roomRateDto.endDate) &&
        Objects.equals(this.pmsRoomType, roomRateDto.pmsRoomType) &&
        Objects.equals(this.rateDisplaySet, roomRateDto.rateDisplaySet) &&
        Objects.equals(this.ratePlanCode, roomRateDto.ratePlanCode) &&
        Objects.equals(this.specialRequests, roomRateDto.specialRequests) &&
        Objects.equals(this.startDate, roomRateDto.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellCode, endDate, pmsRoomType, rateDisplaySet, ratePlanCode, specialRequests, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomRateDto {\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    pmsRoomType: ").append(toIndentedString(pmsRoomType)).append("\n");
    sb.append("    rateDisplaySet: ").append(toIndentedString(rateDisplaySet)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    specialRequests: ").append(toIndentedString(specialRequests)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
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

