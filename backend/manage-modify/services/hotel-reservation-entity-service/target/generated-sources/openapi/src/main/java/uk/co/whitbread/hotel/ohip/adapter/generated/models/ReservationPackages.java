package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConsumptionDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ScheduleList;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationPackages
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPackages {

  private @Nullable ConsumptionDetails consumptionDetails;

  private @Nullable String endDate;

  private @Nullable String packageCode;

  private @Nullable String packageGroup;

  private @Nullable PackageHeaderType packageHeaderType;

  private @Nullable String ratePlanCode;

  @Valid
  private List<@Valid ScheduleList> scheduleList = new ArrayList<>();

  private @Nullable String startDate;

  public ReservationPackages consumptionDetails(ConsumptionDetails consumptionDetails) {
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
  public ConsumptionDetails getConsumptionDetails() {
    return consumptionDetails;
  }

  public void setConsumptionDetails(ConsumptionDetails consumptionDetails) {
    this.consumptionDetails = consumptionDetails;
  }

  public ReservationPackages endDate(String endDate) {
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

  public ReservationPackages packageCode(String packageCode) {
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

  public ReservationPackages packageGroup(String packageGroup) {
    this.packageGroup = packageGroup;
    return this;
  }

  /**
   * Get packageGroup
   * @return packageGroup
   */
  
  @Schema(name = "packageGroup", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageGroup")
  public String getPackageGroup() {
    return packageGroup;
  }

  public void setPackageGroup(String packageGroup) {
    this.packageGroup = packageGroup;
  }

  public ReservationPackages packageHeaderType(PackageHeaderType packageHeaderType) {
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
  public PackageHeaderType getPackageHeaderType() {
    return packageHeaderType;
  }

  public void setPackageHeaderType(PackageHeaderType packageHeaderType) {
    this.packageHeaderType = packageHeaderType;
  }

  public ReservationPackages ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public ReservationPackages scheduleList(List<@Valid ScheduleList> scheduleList) {
    this.scheduleList = scheduleList;
    return this;
  }

  public ReservationPackages addScheduleListItem(ScheduleList scheduleListItem) {
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
  public List<@Valid ScheduleList> getScheduleList() {
    return scheduleList;
  }

  public void setScheduleList(List<@Valid ScheduleList> scheduleList) {
    this.scheduleList = scheduleList;
  }

  public ReservationPackages startDate(String startDate) {
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
    ReservationPackages reservationPackages = (ReservationPackages) o;
    return Objects.equals(this.consumptionDetails, reservationPackages.consumptionDetails) &&
        Objects.equals(this.endDate, reservationPackages.endDate) &&
        Objects.equals(this.packageCode, reservationPackages.packageCode) &&
        Objects.equals(this.packageGroup, reservationPackages.packageGroup) &&
        Objects.equals(this.packageHeaderType, reservationPackages.packageHeaderType) &&
        Objects.equals(this.ratePlanCode, reservationPackages.ratePlanCode) &&
        Objects.equals(this.scheduleList, reservationPackages.scheduleList) &&
        Objects.equals(this.startDate, reservationPackages.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consumptionDetails, endDate, packageCode, packageGroup, packageHeaderType, ratePlanCode, scheduleList, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPackages {\n");
    sb.append("    consumptionDetails: ").append(toIndentedString(consumptionDetails)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    packageCode: ").append(toIndentedString(packageCode)).append("\n");
    sb.append("    packageGroup: ").append(toIndentedString(packageGroup)).append("\n");
    sb.append("    packageHeaderType: ").append(toIndentedString(packageHeaderType)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    scheduleList: ").append(toIndentedString(scheduleList)).append("\n");
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

