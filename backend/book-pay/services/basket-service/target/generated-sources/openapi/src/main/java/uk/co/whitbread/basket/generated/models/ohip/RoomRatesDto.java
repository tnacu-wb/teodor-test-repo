package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.CheckInRatesDto;
import uk.co.whitbread.basket.generated.models.ohip.GuestCountsDto;
import uk.co.whitbread.basket.generated.models.ohip.StayProfilesDto;
import uk.co.whitbread.basket.generated.models.ohip.TotalDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomRatesDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomRatesDto {

  private @Nullable Boolean bogoDiscount;

  private @Nullable Boolean complimentary;

  private @Nullable Boolean discountAllowed;

  private @Nullable String end;

  private @Nullable Boolean fixedRate;

  private @Nullable GuestCountsDto guestCounts;

  private @Nullable Boolean houseUseOnly;

  private @Nullable String marketCode;

  private @Nullable String marketCodeDescription;

  private @Nullable Integer numberOfUnits;

  private @Nullable Boolean pseudoRoom;

  private @Nullable String ratePlanCode;

  private @Nullable CheckInRatesDto rates;

  private @Nullable String roomId;

  private @Nullable String roomType;

  private @Nullable String roomTypeCharged;

  private @Nullable String sourceCode;

  private @Nullable String sourceCodeDescription;

  private @Nullable String start;

  @Valid
  private List<@Valid StayProfilesDto> stayProfiles = new ArrayList<>();

  private @Nullable Boolean suppressRate;

  private @Nullable TotalDto total;

  public RoomRatesDto bogoDiscount(Boolean bogoDiscount) {
    this.bogoDiscount = bogoDiscount;
    return this;
  }

  /**
   * Get bogoDiscount
   * @return bogoDiscount
   */
  
  @Schema(name = "bogoDiscount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bogoDiscount")
  public Boolean getBogoDiscount() {
    return bogoDiscount;
  }

  public void setBogoDiscount(Boolean bogoDiscount) {
    this.bogoDiscount = bogoDiscount;
  }

  public RoomRatesDto complimentary(Boolean complimentary) {
    this.complimentary = complimentary;
    return this;
  }

  /**
   * Get complimentary
   * @return complimentary
   */
  
  @Schema(name = "complimentary", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("complimentary")
  public Boolean getComplimentary() {
    return complimentary;
  }

  public void setComplimentary(Boolean complimentary) {
    this.complimentary = complimentary;
  }

  public RoomRatesDto discountAllowed(Boolean discountAllowed) {
    this.discountAllowed = discountAllowed;
    return this;
  }

  /**
   * Get discountAllowed
   * @return discountAllowed
   */
  
  @Schema(name = "discountAllowed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("discountAllowed")
  public Boolean getDiscountAllowed() {
    return discountAllowed;
  }

  public void setDiscountAllowed(Boolean discountAllowed) {
    this.discountAllowed = discountAllowed;
  }

  public RoomRatesDto end(String end) {
    this.end = end;
    return this;
  }

  /**
   * Get end
   * @return end
   */
  
  @Schema(name = "end", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("end")
  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public RoomRatesDto fixedRate(Boolean fixedRate) {
    this.fixedRate = fixedRate;
    return this;
  }

  /**
   * Get fixedRate
   * @return fixedRate
   */
  
  @Schema(name = "fixedRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fixedRate")
  public Boolean getFixedRate() {
    return fixedRate;
  }

  public void setFixedRate(Boolean fixedRate) {
    this.fixedRate = fixedRate;
  }

  public RoomRatesDto guestCounts(GuestCountsDto guestCounts) {
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

  public RoomRatesDto houseUseOnly(Boolean houseUseOnly) {
    this.houseUseOnly = houseUseOnly;
    return this;
  }

  /**
   * Get houseUseOnly
   * @return houseUseOnly
   */
  
  @Schema(name = "houseUseOnly", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("houseUseOnly")
  public Boolean getHouseUseOnly() {
    return houseUseOnly;
  }

  public void setHouseUseOnly(Boolean houseUseOnly) {
    this.houseUseOnly = houseUseOnly;
  }

  public RoomRatesDto marketCode(String marketCode) {
    this.marketCode = marketCode;
    return this;
  }

  /**
   * Get marketCode
   * @return marketCode
   */
  
  @Schema(name = "marketCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("marketCode")
  public String getMarketCode() {
    return marketCode;
  }

  public void setMarketCode(String marketCode) {
    this.marketCode = marketCode;
  }

  public RoomRatesDto marketCodeDescription(String marketCodeDescription) {
    this.marketCodeDescription = marketCodeDescription;
    return this;
  }

  /**
   * Get marketCodeDescription
   * @return marketCodeDescription
   */
  
  @Schema(name = "marketCodeDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("marketCodeDescription")
  public String getMarketCodeDescription() {
    return marketCodeDescription;
  }

  public void setMarketCodeDescription(String marketCodeDescription) {
    this.marketCodeDescription = marketCodeDescription;
  }

  public RoomRatesDto numberOfUnits(Integer numberOfUnits) {
    this.numberOfUnits = numberOfUnits;
    return this;
  }

  /**
   * Get numberOfUnits
   * @return numberOfUnits
   */
  
  @Schema(name = "numberOfUnits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfUnits")
  public Integer getNumberOfUnits() {
    return numberOfUnits;
  }

  public void setNumberOfUnits(Integer numberOfUnits) {
    this.numberOfUnits = numberOfUnits;
  }

  public RoomRatesDto pseudoRoom(Boolean pseudoRoom) {
    this.pseudoRoom = pseudoRoom;
    return this;
  }

  /**
   * Get pseudoRoom
   * @return pseudoRoom
   */
  
  @Schema(name = "pseudoRoom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pseudoRoom")
  public Boolean getPseudoRoom() {
    return pseudoRoom;
  }

  public void setPseudoRoom(Boolean pseudoRoom) {
    this.pseudoRoom = pseudoRoom;
  }

  public RoomRatesDto ratePlanCode(String ratePlanCode) {
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

  public RoomRatesDto rates(CheckInRatesDto rates) {
    this.rates = rates;
    return this;
  }

  /**
   * Get rates
   * @return rates
   */
  @Valid 
  @Schema(name = "rates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rates")
  public CheckInRatesDto getRates() {
    return rates;
  }

  public void setRates(CheckInRatesDto rates) {
    this.rates = rates;
  }

  public RoomRatesDto roomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  /**
   * Get roomId
   * @return roomId
   */
  
  @Schema(name = "roomId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomId")
  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public RoomRatesDto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public RoomRatesDto roomTypeCharged(String roomTypeCharged) {
    this.roomTypeCharged = roomTypeCharged;
    return this;
  }

  /**
   * Get roomTypeCharged
   * @return roomTypeCharged
   */
  
  @Schema(name = "roomTypeCharged", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypeCharged")
  public String getRoomTypeCharged() {
    return roomTypeCharged;
  }

  public void setRoomTypeCharged(String roomTypeCharged) {
    this.roomTypeCharged = roomTypeCharged;
  }

  public RoomRatesDto sourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
    return this;
  }

  /**
   * Get sourceCode
   * @return sourceCode
   */
  
  @Schema(name = "sourceCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceCode")
  public String getSourceCode() {
    return sourceCode;
  }

  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public RoomRatesDto sourceCodeDescription(String sourceCodeDescription) {
    this.sourceCodeDescription = sourceCodeDescription;
    return this;
  }

  /**
   * Get sourceCodeDescription
   * @return sourceCodeDescription
   */
  
  @Schema(name = "sourceCodeDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceCodeDescription")
  public String getSourceCodeDescription() {
    return sourceCodeDescription;
  }

  public void setSourceCodeDescription(String sourceCodeDescription) {
    this.sourceCodeDescription = sourceCodeDescription;
  }

  public RoomRatesDto start(String start) {
    this.start = start;
    return this;
  }

  /**
   * Get start
   * @return start
   */
  
  @Schema(name = "start", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("start")
  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public RoomRatesDto stayProfiles(List<@Valid StayProfilesDto> stayProfiles) {
    this.stayProfiles = stayProfiles;
    return this;
  }

  public RoomRatesDto addStayProfilesItem(StayProfilesDto stayProfilesItem) {
    if (this.stayProfiles == null) {
      this.stayProfiles = new ArrayList<>();
    }
    this.stayProfiles.add(stayProfilesItem);
    return this;
  }

  /**
   * Get stayProfiles
   * @return stayProfiles
   */
  @Valid 
  @Schema(name = "stayProfiles", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stayProfiles")
  public List<@Valid StayProfilesDto> getStayProfiles() {
    return stayProfiles;
  }

  public void setStayProfiles(List<@Valid StayProfilesDto> stayProfiles) {
    this.stayProfiles = stayProfiles;
  }

  public RoomRatesDto suppressRate(Boolean suppressRate) {
    this.suppressRate = suppressRate;
    return this;
  }

  /**
   * Get suppressRate
   * @return suppressRate
   */
  
  @Schema(name = "suppressRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("suppressRate")
  public Boolean getSuppressRate() {
    return suppressRate;
  }

  public void setSuppressRate(Boolean suppressRate) {
    this.suppressRate = suppressRate;
  }

  public RoomRatesDto total(TotalDto total) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomRatesDto roomRatesDto = (RoomRatesDto) o;
    return Objects.equals(this.bogoDiscount, roomRatesDto.bogoDiscount) &&
        Objects.equals(this.complimentary, roomRatesDto.complimentary) &&
        Objects.equals(this.discountAllowed, roomRatesDto.discountAllowed) &&
        Objects.equals(this.end, roomRatesDto.end) &&
        Objects.equals(this.fixedRate, roomRatesDto.fixedRate) &&
        Objects.equals(this.guestCounts, roomRatesDto.guestCounts) &&
        Objects.equals(this.houseUseOnly, roomRatesDto.houseUseOnly) &&
        Objects.equals(this.marketCode, roomRatesDto.marketCode) &&
        Objects.equals(this.marketCodeDescription, roomRatesDto.marketCodeDescription) &&
        Objects.equals(this.numberOfUnits, roomRatesDto.numberOfUnits) &&
        Objects.equals(this.pseudoRoom, roomRatesDto.pseudoRoom) &&
        Objects.equals(this.ratePlanCode, roomRatesDto.ratePlanCode) &&
        Objects.equals(this.rates, roomRatesDto.rates) &&
        Objects.equals(this.roomId, roomRatesDto.roomId) &&
        Objects.equals(this.roomType, roomRatesDto.roomType) &&
        Objects.equals(this.roomTypeCharged, roomRatesDto.roomTypeCharged) &&
        Objects.equals(this.sourceCode, roomRatesDto.sourceCode) &&
        Objects.equals(this.sourceCodeDescription, roomRatesDto.sourceCodeDescription) &&
        Objects.equals(this.start, roomRatesDto.start) &&
        Objects.equals(this.stayProfiles, roomRatesDto.stayProfiles) &&
        Objects.equals(this.suppressRate, roomRatesDto.suppressRate) &&
        Objects.equals(this.total, roomRatesDto.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bogoDiscount, complimentary, discountAllowed, end, fixedRate, guestCounts, houseUseOnly, marketCode, marketCodeDescription, numberOfUnits, pseudoRoom, ratePlanCode, rates, roomId, roomType, roomTypeCharged, sourceCode, sourceCodeDescription, start, stayProfiles, suppressRate, total);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomRatesDto {\n");
    sb.append("    bogoDiscount: ").append(toIndentedString(bogoDiscount)).append("\n");
    sb.append("    complimentary: ").append(toIndentedString(complimentary)).append("\n");
    sb.append("    discountAllowed: ").append(toIndentedString(discountAllowed)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("    fixedRate: ").append(toIndentedString(fixedRate)).append("\n");
    sb.append("    guestCounts: ").append(toIndentedString(guestCounts)).append("\n");
    sb.append("    houseUseOnly: ").append(toIndentedString(houseUseOnly)).append("\n");
    sb.append("    marketCode: ").append(toIndentedString(marketCode)).append("\n");
    sb.append("    marketCodeDescription: ").append(toIndentedString(marketCodeDescription)).append("\n");
    sb.append("    numberOfUnits: ").append(toIndentedString(numberOfUnits)).append("\n");
    sb.append("    pseudoRoom: ").append(toIndentedString(pseudoRoom)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    rates: ").append(toIndentedString(rates)).append("\n");
    sb.append("    roomId: ").append(toIndentedString(roomId)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    roomTypeCharged: ").append(toIndentedString(roomTypeCharged)).append("\n");
    sb.append("    sourceCode: ").append(toIndentedString(sourceCode)).append("\n");
    sb.append("    sourceCodeDescription: ").append(toIndentedString(sourceCodeDescription)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    stayProfiles: ").append(toIndentedString(stayProfiles)).append("\n");
    sb.append("    suppressRate: ").append(toIndentedString(suppressRate)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
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

