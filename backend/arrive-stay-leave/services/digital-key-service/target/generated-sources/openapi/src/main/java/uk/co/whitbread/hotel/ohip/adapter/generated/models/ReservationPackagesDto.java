package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConsumptionDetailsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageHeaderTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ScheduleListDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationPackagesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPackagesDto {

  private @Nullable ConsumptionDetailsDto consumptionDetails;

  private @Nullable String endDate;

  private @Nullable Double internalID;

  private @Nullable String packageCode;

  private @Nullable PackageHeaderTypeDto packageHeaderType;

  @Valid
  private @Nullable List<@Valid ScheduleListDto> scheduleList;

  private @Nullable String source;

  private @Nullable String startDate;

  public ReservationPackagesDto consumptionDetails(ConsumptionDetailsDto consumptionDetails) {
    this.consumptionDetails = consumptionDetails;
    return this;
  }

  /**
   * Get consumptionDetails
   * @return consumptionDetails
   */
  @Valid 
  @Schema(name = "consumptionDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("consumptionDetails")
  public ConsumptionDetailsDto getConsumptionDetails() {
    return consumptionDetails;
  }

  public void setConsumptionDetails(ConsumptionDetailsDto consumptionDetails) {
    this.consumptionDetails = consumptionDetails;
  }

  public ReservationPackagesDto endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
   */
  
  @Schema(name = "endDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endDate")
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public ReservationPackagesDto internalID(Double internalID) {
    this.internalID = internalID;
    return this;
  }

  /**
   * Get internalID
   * @return internalID
   */
  
  @Schema(name = "internalID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("internalID")
  public Double getInternalID() {
    return internalID;
  }

  public void setInternalID(Double internalID) {
    this.internalID = internalID;
  }

  public ReservationPackagesDto packageCode(String packageCode) {
    this.packageCode = packageCode;
    return this;
  }

  /**
   * Get packageCode
   * @return packageCode
   */
  
  @Schema(name = "packageCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageCode")
  public String getPackageCode() {
    return packageCode;
  }

  public void setPackageCode(String packageCode) {
    this.packageCode = packageCode;
  }

  public ReservationPackagesDto packageHeaderType(PackageHeaderTypeDto packageHeaderType) {
    this.packageHeaderType = packageHeaderType;
    return this;
  }

  /**
   * Get packageHeaderType
   * @return packageHeaderType
   */
  @Valid 
  @Schema(name = "packageHeaderType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageHeaderType")
  public PackageHeaderTypeDto getPackageHeaderType() {
    return packageHeaderType;
  }

  public void setPackageHeaderType(PackageHeaderTypeDto packageHeaderType) {
    this.packageHeaderType = packageHeaderType;
  }

  public ReservationPackagesDto scheduleList(List<@Valid ScheduleListDto> scheduleList) {
    this.scheduleList = scheduleList;
    return this;
  }

  public ReservationPackagesDto addScheduleListItem(ScheduleListDto scheduleListItem) {
    if (this.scheduleList == null) {
      this.scheduleList = new ArrayList<>();
    }
    this.scheduleList.add(scheduleListItem);
    return this;
  }

  /**
   * Get scheduleList
   * @return scheduleList
   */
  @Valid 
  @Schema(name = "scheduleList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scheduleList")
  public List<@Valid ScheduleListDto> getScheduleList() {
    return scheduleList;
  }

  public void setScheduleList(List<@Valid ScheduleListDto> scheduleList) {
    this.scheduleList = scheduleList;
  }

  public ReservationPackagesDto source(String source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
   */
  
  @Schema(name = "source", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("source")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public ReservationPackagesDto startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  
  @Schema(name = "startDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startDate")
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
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
    ReservationPackagesDto reservationPackagesDto = (ReservationPackagesDto) o;
    return Objects.equals(this.consumptionDetails, reservationPackagesDto.consumptionDetails) &&
        Objects.equals(this.endDate, reservationPackagesDto.endDate) &&
        Objects.equals(this.internalID, reservationPackagesDto.internalID) &&
        Objects.equals(this.packageCode, reservationPackagesDto.packageCode) &&
        Objects.equals(this.packageHeaderType, reservationPackagesDto.packageHeaderType) &&
        Objects.equals(this.scheduleList, reservationPackagesDto.scheduleList) &&
        Objects.equals(this.source, reservationPackagesDto.source) &&
        Objects.equals(this.startDate, reservationPackagesDto.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consumptionDetails, endDate, internalID, packageCode, packageHeaderType, scheduleList, source, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPackagesDto {\n");
    sb.append("    consumptionDetails: ").append(toIndentedString(consumptionDetails)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    internalID: ").append(toIndentedString(internalID)).append("\n");
    sb.append("    packageCode: ").append(toIndentedString(packageCode)).append("\n");
    sb.append("    packageHeaderType: ").append(toIndentedString(packageHeaderType)).append("\n");
    sb.append("    scheduleList: ").append(toIndentedString(scheduleList)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
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

