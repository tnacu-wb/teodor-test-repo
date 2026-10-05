package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferCommissionableStatus;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.RequestRatePlans;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.RequestRoomAmenity;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Hotel Offers Request
 */

@Schema(name = "PropertyOffersRequest", description = "Hotel Offers Request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertyOffersRequest {

  private Integer adults = 1;

  private Integer children = 0;

  @Valid
  private List<@Min(0) @Max(18)Integer> childrenAges = new ArrayList<>();

  private Integer numberOfUnits = 1;

  private Boolean ratePlanCodeMatchOnly = false;

  /**
   * This is supported with ratePlanCodeMatchOnly flag as false: <p>  <strong>Always</strong> - return alternate offers </p> <p>  <strong>WhenRequestedNotAvailable</strong> - return alternate rates if requested rates are not available </p>
   */
  public enum AlternateOffersEnum {
    ALWAYS("Always"),
    
    WHEN_REQUESTED_NOT_AVAILABLE("WhenRequestedNotAvailable");

    private String value;

    AlternateOffersEnum(String value) {
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
    public static AlternateOffersEnum fromValue(String value) {
      for (AlternateOffersEnum b : AlternateOffersEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private AlternateOffersEnum alternateOffers = AlternateOffersEnum.ALWAYS;

  private OfferCommissionableStatus commissionableStatus = OfferCommissionableStatus.BOTH;

  private @Nullable BigDecimal minRate;

  private @Nullable BigDecimal maxRate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate arrivalDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate departureDate;

  private Boolean roomTypeMatchOnly = false;

  @Valid
  private List<@Size(min = 1, max = 50)String> roomTypes = new ArrayList<>();

  /**
   * The rate mode to be applied. It represents a shown rate is highest, average,first night, or most frequent one when there are rate changes during the staty duration. <p> <strong>Highest</strong> - Indicates the rate is highest</p> <p> <strong>Average</strong> - Indicates the rate is avarage. </p> <p> <strong>Arrival </strong> - Indicates the rate is for the first night. </p> <p> <strong>MostFrequent</strong> - Indicates the rate is most frequent withing all nights. </p>
   */
  public enum RateModeEnum {
    HIGHEST("Highest"),
    
    AVERAGE("Average"),
    
    ARRIVAL("Arrival"),
    
    MOST_FREQUENT("MostFrequent");

    private String value;

    RateModeEnum(String value) {
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
    public static RateModeEnum fromValue(String value) {
      for (RateModeEnum b : RateModeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private RateModeEnum rateMode = RateModeEnum.HIGHEST;

  @Valid
  private List<@Valid RequestRatePlans> ratePlans = new ArrayList<>();

  @Valid
  private List<@Valid RequestRoomAmenity> roomAmenities = new ArrayList<>();

  private Boolean includeAmenities = false;

  @Valid
  private List<@Size(min = 1, max = 15)String> promotionCodes = new ArrayList<>();

  public PropertyOffersRequest adults(Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Number of adults
   * minimum: 1
   * maximum: 10
   * @return adults
   */
  @Min(1) @Max(10) 
  @Schema(name = "adults", example = "1", description = "Number of adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public Integer getAdults() {
    return adults;
  }

  public void setAdults(Integer adults) {
    this.adults = adults;
  }

  public PropertyOffersRequest children(Integer children) {
    this.children = children;
    return this;
  }

  /**
   * Number of children
   * minimum: 0
   * maximum: 10
   * @return children
   */
  @Min(0) @Max(10) 
  @Schema(name = "children", example = "2", description = "Number of children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public Integer getChildren() {
    return children;
  }

  public void setChildren(Integer children) {
    this.children = children;
  }

  public PropertyOffersRequest childrenAges(List<@Min(0) @Max(18)Integer> childrenAges) {
    this.childrenAges = childrenAges;
    return this;
  }

  public PropertyOffersRequest addChildrenAgesItem(Integer childrenAgesItem) {
    if (this.childrenAges == null) {
      this.childrenAges = new ArrayList<>();
    }
    this.childrenAges.add(childrenAgesItem);
    return this;
  }

  /**
   * List of Age of the children. Element count must match the number of children
   * @return childrenAges
   */
  
  @Schema(name = "childrenAges", description = "List of Age of the children. Element count must match the number of children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childrenAges")
  public List<@Min(0) @Max(18)Integer> getChildrenAges() {
    return childrenAges;
  }

  public void setChildrenAges(List<@Min(0) @Max(18)Integer> childrenAges) {
    this.childrenAges = childrenAges;
  }

  public PropertyOffersRequest numberOfUnits(Integer numberOfUnits) {
    this.numberOfUnits = numberOfUnits;
    return this;
  }

  /**
   * Number of units
   * minimum: 1
   * maximum: 10
   * @return numberOfUnits
   */
  @Min(1) @Max(10) 
  @Schema(name = "numberOfUnits", example = "1", description = "Number of units", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfUnits")
  public Integer getNumberOfUnits() {
    return numberOfUnits;
  }

  public void setNumberOfUnits(Integer numberOfUnits) {
    this.numberOfUnits = numberOfUnits;
  }

  public PropertyOffersRequest ratePlanCodeMatchOnly(Boolean ratePlanCodeMatchOnly) {
    this.ratePlanCodeMatchOnly = ratePlanCodeMatchOnly;
    return this;
  }

  /**
   * If true, only rate plan code specified, otherwise public rate plan codes too
   * @return ratePlanCodeMatchOnly
   */
  
  @Schema(name = "ratePlanCodeMatchOnly", description = "If true, only rate plan code specified, otherwise public rate plan codes too", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCodeMatchOnly")
  public Boolean getRatePlanCodeMatchOnly() {
    return ratePlanCodeMatchOnly;
  }

  public void setRatePlanCodeMatchOnly(Boolean ratePlanCodeMatchOnly) {
    this.ratePlanCodeMatchOnly = ratePlanCodeMatchOnly;
  }

  public PropertyOffersRequest alternateOffers(AlternateOffersEnum alternateOffers) {
    this.alternateOffers = alternateOffers;
    return this;
  }

  /**
   * This is supported with ratePlanCodeMatchOnly flag as false: <p>  <strong>Always</strong> - return alternate offers </p> <p>  <strong>WhenRequestedNotAvailable</strong> - return alternate rates if requested rates are not available </p>
   * @return alternateOffers
   */
  
  @Schema(name = "alternateOffers", description = "This is supported with ratePlanCodeMatchOnly flag as false: <p>  <strong>Always</strong> - return alternate offers </p> <p>  <strong>WhenRequestedNotAvailable</strong> - return alternate rates if requested rates are not available </p>", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alternateOffers")
  public AlternateOffersEnum getAlternateOffers() {
    return alternateOffers;
  }

  public void setAlternateOffers(AlternateOffersEnum alternateOffers) {
    this.alternateOffers = alternateOffers;
  }

  public PropertyOffersRequest commissionableStatus(OfferCommissionableStatus commissionableStatus) {
    this.commissionableStatus = commissionableStatus;
    return this;
  }

  /**
   * Get commissionableStatus
   * @return commissionableStatus
   */
  @Valid 
  @Schema(name = "commissionableStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("commissionableStatus")
  public OfferCommissionableStatus getCommissionableStatus() {
    return commissionableStatus;
  }

  public void setCommissionableStatus(OfferCommissionableStatus commissionableStatus) {
    this.commissionableStatus = commissionableStatus;
  }

  public PropertyOffersRequest minRate(BigDecimal minRate) {
    this.minRate = minRate;
    return this;
  }

  /**
   * Minimum base rate in an offer
   * @return minRate
   */
  @Valid 
  @Schema(name = "minRate", example = "24.12", description = "Minimum base rate in an offer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minRate")
  public BigDecimal getMinRate() {
    return minRate;
  }

  public void setMinRate(BigDecimal minRate) {
    this.minRate = minRate;
  }

  public PropertyOffersRequest maxRate(BigDecimal maxRate) {
    this.maxRate = maxRate;
    return this;
  }

  /**
   * Maximum base rate in an offer
   * @return maxRate
   */
  @Valid 
  @Schema(name = "maxRate", example = "24.12", description = "Maximum base rate in an offer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxRate")
  public BigDecimal getMaxRate() {
    return maxRate;
  }

  public void setMaxRate(BigDecimal maxRate) {
    this.maxRate = maxRate;
  }

  public PropertyOffersRequest arrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Arrival/Check-in Date
   * @return arrivalDate
   */
  @Valid 
  @Schema(name = "arrivalDate", example = "2021-06-01", description = "Arrival/Check-in Date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public LocalDate getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public PropertyOffersRequest departureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Departure/Check-out Date
   * @return departureDate
   */
  @Valid 
  @Schema(name = "departureDate", example = "2021-06-07", description = "Departure/Check-out Date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public LocalDate getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
  }

  public PropertyOffersRequest roomTypeMatchOnly(Boolean roomTypeMatchOnly) {
    this.roomTypeMatchOnly = roomTypeMatchOnly;
    return this;
  }

  /**
   * If true, only room types specified, otherwise all avaleble room types will be shown.
   * @return roomTypeMatchOnly
   */
  
  @Schema(name = "roomTypeMatchOnly", example = "false", description = "If true, only room types specified, otherwise all avaleble room types will be shown.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypeMatchOnly")
  public Boolean getRoomTypeMatchOnly() {
    return roomTypeMatchOnly;
  }

  public void setRoomTypeMatchOnly(Boolean roomTypeMatchOnly) {
    this.roomTypeMatchOnly = roomTypeMatchOnly;
  }

  public PropertyOffersRequest roomTypes(List<@Size(min = 1, max = 50)String> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public PropertyOffersRequest addRoomTypesItem(String roomTypesItem) {
    if (this.roomTypes == null) {
      this.roomTypes = new ArrayList<>();
    }
    this.roomTypes.add(roomTypesItem);
    return this;
  }

  /**
   * List of Room Type codes
   * @return roomTypes
   */
  @Size(min = 0, max = 15) 
  @Schema(name = "roomTypes", example = "[XA1K, XB1K]", description = "List of Room Type codes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypes")
  public List<@Size(min = 1, max = 50)String> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(List<@Size(min = 1, max = 50)String> roomTypes) {
    this.roomTypes = roomTypes;
  }

  public PropertyOffersRequest rateMode(RateModeEnum rateMode) {
    this.rateMode = rateMode;
    return this;
  }

  /**
   * The rate mode to be applied. It represents a shown rate is highest, average,first night, or most frequent one when there are rate changes during the staty duration. <p> <strong>Highest</strong> - Indicates the rate is highest</p> <p> <strong>Average</strong> - Indicates the rate is avarage. </p> <p> <strong>Arrival </strong> - Indicates the rate is for the first night. </p> <p> <strong>MostFrequent</strong> - Indicates the rate is most frequent withing all nights. </p>
   * @return rateMode
   */
  
  @Schema(name = "rateMode", example = "Highest", description = "The rate mode to be applied. It represents a shown rate is highest, average,first night, or most frequent one when there are rate changes during the staty duration. <p> <strong>Highest</strong> - Indicates the rate is highest</p> <p> <strong>Average</strong> - Indicates the rate is avarage. </p> <p> <strong>Arrival </strong> - Indicates the rate is for the first night. </p> <p> <strong>MostFrequent</strong> - Indicates the rate is most frequent withing all nights. </p>", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateMode")
  public RateModeEnum getRateMode() {
    return rateMode;
  }

  public void setRateMode(RateModeEnum rateMode) {
    this.rateMode = rateMode;
  }

  public PropertyOffersRequest ratePlans(List<@Valid RequestRatePlans> ratePlans) {
    this.ratePlans = ratePlans;
    return this;
  }

  public PropertyOffersRequest addRatePlansItem(RequestRatePlans ratePlansItem) {
    if (this.ratePlans == null) {
      this.ratePlans = new ArrayList<>();
    }
    this.ratePlans.add(ratePlansItem);
    return this;
  }

  /**
   * Collection of Rate Plans
   * @return ratePlans
   */
  @Valid @Size(max = 15) 
  @Schema(name = "ratePlans", description = "Collection of Rate Plans", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlans")
  public List<@Valid RequestRatePlans> getRatePlans() {
    return ratePlans;
  }

  public void setRatePlans(List<@Valid RequestRatePlans> ratePlans) {
    this.ratePlans = ratePlans;
  }

  public PropertyOffersRequest roomAmenities(List<@Valid RequestRoomAmenity> roomAmenities) {
    this.roomAmenities = roomAmenities;
    return this;
  }

  public PropertyOffersRequest addRoomAmenitiesItem(RequestRoomAmenity roomAmenitiesItem) {
    if (this.roomAmenities == null) {
      this.roomAmenities = new ArrayList<>();
    }
    this.roomAmenities.add(roomAmenitiesItem);
    return this;
  }

  /**
   * Requested specific amenities configured for the room
   * @return roomAmenities
   */
  @Valid @Size(max = 10) 
  @Schema(name = "roomAmenities", description = "Requested specific amenities configured for the room", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomAmenities")
  public List<@Valid RequestRoomAmenity> getRoomAmenities() {
    return roomAmenities;
  }

  public void setRoomAmenities(List<@Valid RequestRoomAmenity> roomAmenities) {
    this.roomAmenities = roomAmenities;
  }

  public PropertyOffersRequest includeAmenities(Boolean includeAmenities) {
    this.includeAmenities = includeAmenities;
    return this;
  }

  /**
   * Indicates to include property amenities in the response
   * @return includeAmenities
   */
  
  @Schema(name = "includeAmenities", description = "Indicates to include property amenities in the response", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("includeAmenities")
  public Boolean getIncludeAmenities() {
    return includeAmenities;
  }

  public void setIncludeAmenities(Boolean includeAmenities) {
    this.includeAmenities = includeAmenities;
  }

  public PropertyOffersRequest promotionCodes(List<@Size(min = 1, max = 15)String> promotionCodes) {
    this.promotionCodes = promotionCodes;
    return this;
  }

  public PropertyOffersRequest addPromotionCodesItem(String promotionCodesItem) {
    if (this.promotionCodes == null) {
      this.promotionCodes = new ArrayList<>();
    }
    this.promotionCodes.add(promotionCodesItem);
    return this;
  }

  /**
   * Collection of promotionCodes
   * @return promotionCodes
   */
  @Size(min = 1) 
  @Schema(name = "promotionCodes", example = "PROMO1", description = "Collection of promotionCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionCodes")
  public List<@Size(min = 1, max = 15)String> getPromotionCodes() {
    return promotionCodes;
  }

  public void setPromotionCodes(List<@Size(min = 1, max = 15)String> promotionCodes) {
    this.promotionCodes = promotionCodes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertyOffersRequest propertyOffersRequest = (PropertyOffersRequest) o;
    return Objects.equals(this.adults, propertyOffersRequest.adults) &&
        Objects.equals(this.children, propertyOffersRequest.children) &&
        Objects.equals(this.childrenAges, propertyOffersRequest.childrenAges) &&
        Objects.equals(this.numberOfUnits, propertyOffersRequest.numberOfUnits) &&
        Objects.equals(this.ratePlanCodeMatchOnly, propertyOffersRequest.ratePlanCodeMatchOnly) &&
        Objects.equals(this.alternateOffers, propertyOffersRequest.alternateOffers) &&
        Objects.equals(this.commissionableStatus, propertyOffersRequest.commissionableStatus) &&
        Objects.equals(this.minRate, propertyOffersRequest.minRate) &&
        Objects.equals(this.maxRate, propertyOffersRequest.maxRate) &&
        Objects.equals(this.arrivalDate, propertyOffersRequest.arrivalDate) &&
        Objects.equals(this.departureDate, propertyOffersRequest.departureDate) &&
        Objects.equals(this.roomTypeMatchOnly, propertyOffersRequest.roomTypeMatchOnly) &&
        Objects.equals(this.roomTypes, propertyOffersRequest.roomTypes) &&
        Objects.equals(this.rateMode, propertyOffersRequest.rateMode) &&
        Objects.equals(this.ratePlans, propertyOffersRequest.ratePlans) &&
        Objects.equals(this.roomAmenities, propertyOffersRequest.roomAmenities) &&
        Objects.equals(this.includeAmenities, propertyOffersRequest.includeAmenities) &&
        Objects.equals(this.promotionCodes, propertyOffersRequest.promotionCodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, children, childrenAges, numberOfUnits, ratePlanCodeMatchOnly, alternateOffers, commissionableStatus, minRate, maxRate, arrivalDate, departureDate, roomTypeMatchOnly, roomTypes, rateMode, ratePlans, roomAmenities, includeAmenities, promotionCodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertyOffersRequest {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    childrenAges: ").append(toIndentedString(childrenAges)).append("\n");
    sb.append("    numberOfUnits: ").append(toIndentedString(numberOfUnits)).append("\n");
    sb.append("    ratePlanCodeMatchOnly: ").append(toIndentedString(ratePlanCodeMatchOnly)).append("\n");
    sb.append("    alternateOffers: ").append(toIndentedString(alternateOffers)).append("\n");
    sb.append("    commissionableStatus: ").append(toIndentedString(commissionableStatus)).append("\n");
    sb.append("    minRate: ").append(toIndentedString(minRate)).append("\n");
    sb.append("    maxRate: ").append(toIndentedString(maxRate)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    roomTypeMatchOnly: ").append(toIndentedString(roomTypeMatchOnly)).append("\n");
    sb.append("    roomTypes: ").append(toIndentedString(roomTypes)).append("\n");
    sb.append("    rateMode: ").append(toIndentedString(rateMode)).append("\n");
    sb.append("    ratePlans: ").append(toIndentedString(ratePlans)).append("\n");
    sb.append("    roomAmenities: ").append(toIndentedString(roomAmenities)).append("\n");
    sb.append("    includeAmenities: ").append(toIndentedString(includeAmenities)).append("\n");
    sb.append("    promotionCodes: ").append(toIndentedString(promotionCodes)).append("\n");
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

