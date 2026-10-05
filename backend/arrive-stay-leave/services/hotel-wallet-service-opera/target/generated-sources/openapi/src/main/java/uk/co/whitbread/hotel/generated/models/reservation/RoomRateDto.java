package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.RatePriceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomRateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomRateDto {

  private @Nullable String cellCode;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate endDate;

  private String pmsRoomType;

  /**
   * Gets or Sets promoKind
   */
  public enum PromoKindEnum {
    LANDING_PAGE("LANDING_PAGE"),
    
    SITE_WIDE("SITE_WIDE"),
    
    GENERIC("GENERIC"),
    
    UNIQUE("UNIQUE");

    private String value;

    PromoKindEnum(String value) {
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
    public static PromoKindEnum fromValue(String value) {
      for (PromoKindEnum b : PromoKindEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable PromoKindEnum promoKind;

  private @Nullable String promotionCode;

  private @Nullable String rateDisplaySet;

  private String ratePlanCode;

  @Valid
  private List<@Valid RatePriceDto> ratePrices = new ArrayList<>();

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

  public RoomRateDto promoKind(PromoKindEnum promoKind) {
    this.promoKind = promoKind;
    return this;
  }

  /**
   * Get promoKind
   * @return promoKind
   */
  
  @Schema(name = "promoKind", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoKind")
  public PromoKindEnum getPromoKind() {
    return promoKind;
  }

  public void setPromoKind(PromoKindEnum promoKind) {
    this.promoKind = promoKind;
  }

  public RoomRateDto promotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
    return this;
  }

  /**
   * Get promotionCode
   * @return promotionCode
   */
  
  @Schema(name = "promotionCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionCode")
  public String getPromotionCode() {
    return promotionCode;
  }

  public void setPromotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
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

  public RoomRateDto ratePrices(List<@Valid RatePriceDto> ratePrices) {
    this.ratePrices = ratePrices;
    return this;
  }

  public RoomRateDto addRatePricesItem(RatePriceDto ratePricesItem) {
    if (this.ratePrices == null) {
      this.ratePrices = new ArrayList<>();
    }
    this.ratePrices.add(ratePricesItem);
    return this;
  }

  /**
   * Get ratePrices
   * @return ratePrices
   */
  @Valid 
  @Schema(name = "ratePrices", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePrices")
  public List<@Valid RatePriceDto> getRatePrices() {
    return ratePrices;
  }

  public void setRatePrices(List<@Valid RatePriceDto> ratePrices) {
    this.ratePrices = ratePrices;
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
        Objects.equals(this.promoKind, roomRateDto.promoKind) &&
        Objects.equals(this.promotionCode, roomRateDto.promotionCode) &&
        Objects.equals(this.rateDisplaySet, roomRateDto.rateDisplaySet) &&
        Objects.equals(this.ratePlanCode, roomRateDto.ratePlanCode) &&
        Objects.equals(this.ratePrices, roomRateDto.ratePrices) &&
        Objects.equals(this.specialRequests, roomRateDto.specialRequests) &&
        Objects.equals(this.startDate, roomRateDto.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellCode, endDate, pmsRoomType, promoKind, promotionCode, rateDisplaySet, ratePlanCode, ratePrices, specialRequests, startDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomRateDto {\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    pmsRoomType: ").append(toIndentedString(pmsRoomType)).append("\n");
    sb.append("    promoKind: ").append(toIndentedString(promoKind)).append("\n");
    sb.append("    promotionCode: ").append(toIndentedString(promotionCode)).append("\n");
    sb.append("    rateDisplaySet: ").append(toIndentedString(rateDisplaySet)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    ratePrices: ").append(toIndentedString(ratePrices)).append("\n");
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

