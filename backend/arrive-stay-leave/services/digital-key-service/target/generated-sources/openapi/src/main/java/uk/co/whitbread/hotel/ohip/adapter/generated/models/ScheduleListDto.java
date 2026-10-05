package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ScheduleListDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ScheduleListDto {

  private @Nullable Float computedResvPrice;

  private @Nullable String consumptionDate;

  private @Nullable Integer originalUnitAllowance;

  private @Nullable Float originalUnitPrice;

  private @Nullable String reservationDate;

  private @Nullable Integer totalQuantity;

  private @Nullable Integer unitAllowance;

  private @Nullable BigDecimal unitPrice;

  public ScheduleListDto computedResvPrice(Float computedResvPrice) {
    this.computedResvPrice = computedResvPrice;
    return this;
  }

  /**
   * Get computedResvPrice
   * @return computedResvPrice
   */
  
  @Schema(name = "computedResvPrice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("computedResvPrice")
  public Float getComputedResvPrice() {
    return computedResvPrice;
  }

  public void setComputedResvPrice(Float computedResvPrice) {
    this.computedResvPrice = computedResvPrice;
  }

  public ScheduleListDto consumptionDate(String consumptionDate) {
    this.consumptionDate = consumptionDate;
    return this;
  }

  /**
   * Get consumptionDate
   * @return consumptionDate
   */
  
  @Schema(name = "consumptionDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("consumptionDate")
  public String getConsumptionDate() {
    return consumptionDate;
  }

  public void setConsumptionDate(String consumptionDate) {
    this.consumptionDate = consumptionDate;
  }

  public ScheduleListDto originalUnitAllowance(Integer originalUnitAllowance) {
    this.originalUnitAllowance = originalUnitAllowance;
    return this;
  }

  /**
   * Get originalUnitAllowance
   * @return originalUnitAllowance
   */
  
  @Schema(name = "originalUnitAllowance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalUnitAllowance")
  public Integer getOriginalUnitAllowance() {
    return originalUnitAllowance;
  }

  public void setOriginalUnitAllowance(Integer originalUnitAllowance) {
    this.originalUnitAllowance = originalUnitAllowance;
  }

  public ScheduleListDto originalUnitPrice(Float originalUnitPrice) {
    this.originalUnitPrice = originalUnitPrice;
    return this;
  }

  /**
   * Get originalUnitPrice
   * @return originalUnitPrice
   */
  
  @Schema(name = "originalUnitPrice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalUnitPrice")
  public Float getOriginalUnitPrice() {
    return originalUnitPrice;
  }

  public void setOriginalUnitPrice(Float originalUnitPrice) {
    this.originalUnitPrice = originalUnitPrice;
  }

  public ScheduleListDto reservationDate(String reservationDate) {
    this.reservationDate = reservationDate;
    return this;
  }

  /**
   * Get reservationDate
   * @return reservationDate
   */
  
  @Schema(name = "reservationDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationDate")
  public String getReservationDate() {
    return reservationDate;
  }

  public void setReservationDate(String reservationDate) {
    this.reservationDate = reservationDate;
  }

  public ScheduleListDto totalQuantity(Integer totalQuantity) {
    this.totalQuantity = totalQuantity;
    return this;
  }

  /**
   * Get totalQuantity
   * @return totalQuantity
   */
  
  @Schema(name = "totalQuantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalQuantity")
  public Integer getTotalQuantity() {
    return totalQuantity;
  }

  public void setTotalQuantity(Integer totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  public ScheduleListDto unitAllowance(Integer unitAllowance) {
    this.unitAllowance = unitAllowance;
    return this;
  }

  /**
   * Get unitAllowance
   * @return unitAllowance
   */
  
  @Schema(name = "unitAllowance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("unitAllowance")
  public Integer getUnitAllowance() {
    return unitAllowance;
  }

  public void setUnitAllowance(Integer unitAllowance) {
    this.unitAllowance = unitAllowance;
  }

  public ScheduleListDto unitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
    return this;
  }

  /**
   * Get unitPrice
   * @return unitPrice
   */
  @Valid 
  @Schema(name = "unitPrice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("unitPrice")
  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScheduleListDto scheduleListDto = (ScheduleListDto) o;
    return Objects.equals(this.computedResvPrice, scheduleListDto.computedResvPrice) &&
        Objects.equals(this.consumptionDate, scheduleListDto.consumptionDate) &&
        Objects.equals(this.originalUnitAllowance, scheduleListDto.originalUnitAllowance) &&
        Objects.equals(this.originalUnitPrice, scheduleListDto.originalUnitPrice) &&
        Objects.equals(this.reservationDate, scheduleListDto.reservationDate) &&
        Objects.equals(this.totalQuantity, scheduleListDto.totalQuantity) &&
        Objects.equals(this.unitAllowance, scheduleListDto.unitAllowance) &&
        Objects.equals(this.unitPrice, scheduleListDto.unitPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(computedResvPrice, consumptionDate, originalUnitAllowance, originalUnitPrice, reservationDate, totalQuantity, unitAllowance, unitPrice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ScheduleListDto {\n");
    sb.append("    computedResvPrice: ").append(toIndentedString(computedResvPrice)).append("\n");
    sb.append("    consumptionDate: ").append(toIndentedString(consumptionDate)).append("\n");
    sb.append("    originalUnitAllowance: ").append(toIndentedString(originalUnitAllowance)).append("\n");
    sb.append("    originalUnitPrice: ").append(toIndentedString(originalUnitPrice)).append("\n");
    sb.append("    reservationDate: ").append(toIndentedString(reservationDate)).append("\n");
    sb.append("    totalQuantity: ").append(toIndentedString(totalQuantity)).append("\n");
    sb.append("    unitAllowance: ").append(toIndentedString(unitAllowance)).append("\n");
    sb.append("    unitPrice: ").append(toIndentedString(unitPrice)).append("\n");
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

