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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRateMode;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.RequestRatePlans;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PropertySearchRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertySearchRequest {

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

  @Valid
  private List<@Size(min = 1, max = 50)String> hotelCodes = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate arrivalDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate departureDate;

  private @Nullable OfferRateMode rateMode;

  @Valid
  private List<@Valid RequestRatePlans> ratePlans = new ArrayList<>();

  private Boolean availableOnly = false;

  @Valid
  private List<@Size(min = 1, max = 50)String> promotionCodes = new ArrayList<>();

  public PropertySearchRequest adults(Integer adults) {
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

  public PropertySearchRequest children(Integer children) {
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

  public PropertySearchRequest childrenAges(List<@Min(0) @Max(18)Integer> childrenAges) {
    this.childrenAges = childrenAges;
    return this;
  }

  public PropertySearchRequest addChildrenAgesItem(Integer childrenAgesItem) {
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

  public PropertySearchRequest numberOfUnits(Integer numberOfUnits) {
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

  public PropertySearchRequest ratePlanCodeMatchOnly(Boolean ratePlanCodeMatchOnly) {
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

  public PropertySearchRequest alternateOffers(AlternateOffersEnum alternateOffers) {
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

  public PropertySearchRequest commissionableStatus(OfferCommissionableStatus commissionableStatus) {
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

  public PropertySearchRequest minRate(BigDecimal minRate) {
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

  public PropertySearchRequest maxRate(BigDecimal maxRate) {
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

  public PropertySearchRequest hotelCodes(List<@Size(min = 1, max = 50)String> hotelCodes) {
    this.hotelCodes = hotelCodes;
    return this;
  }

  public PropertySearchRequest addHotelCodesItem(String hotelCodesItem) {
    if (this.hotelCodes == null) {
      this.hotelCodes = new ArrayList<>();
    }
    this.hotelCodes.add(hotelCodesItem);
    return this;
  }

  /**
   * List of Hotel Codes
   * @return hotelCodes
   */
  @Size(min = 1) 
  @Schema(name = "hotelCodes", example = "XUSXXYY99", description = "List of Hotel Codes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCodes")
  public List<@Size(min = 1, max = 50)String> getHotelCodes() {
    return hotelCodes;
  }

  public void setHotelCodes(List<@Size(min = 1, max = 50)String> hotelCodes) {
    this.hotelCodes = hotelCodes;
  }

  public PropertySearchRequest arrivalDate(LocalDate arrivalDate) {
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

  public PropertySearchRequest departureDate(LocalDate departureDate) {
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

  public PropertySearchRequest rateMode(OfferRateMode rateMode) {
    this.rateMode = rateMode;
    return this;
  }

  /**
   * Get rateMode
   * @return rateMode
   */
  @Valid 
  @Schema(name = "rateMode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateMode")
  public OfferRateMode getRateMode() {
    return rateMode;
  }

  public void setRateMode(OfferRateMode rateMode) {
    this.rateMode = rateMode;
  }

  public PropertySearchRequest ratePlans(List<@Valid RequestRatePlans> ratePlans) {
    this.ratePlans = ratePlans;
    return this;
  }

  public PropertySearchRequest addRatePlansItem(RequestRatePlans ratePlansItem) {
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
  @Valid @Size(min = 0, max = 15) 
  @Schema(name = "ratePlans", description = "Collection of Rate Plans", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlans")
  public List<@Valid RequestRatePlans> getRatePlans() {
    return ratePlans;
  }

  public void setRatePlans(List<@Valid RequestRatePlans> ratePlans) {
    this.ratePlans = ratePlans;
  }

  public PropertySearchRequest availableOnly(Boolean availableOnly) {
    this.availableOnly = availableOnly;
    return this;
  }

  /**
   * If true, only hotels with availability will be returned (except if all of them are unavailable)
   * @return availableOnly
   */
  
  @Schema(name = "availableOnly", description = "If true, only hotels with availability will be returned (except if all of them are unavailable)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availableOnly")
  public Boolean getAvailableOnly() {
    return availableOnly;
  }

  public void setAvailableOnly(Boolean availableOnly) {
    this.availableOnly = availableOnly;
  }

  public PropertySearchRequest promotionCodes(List<@Size(min = 1, max = 50)String> promotionCodes) {
    this.promotionCodes = promotionCodes;
    return this;
  }

  public PropertySearchRequest addPromotionCodesItem(String promotionCodesItem) {
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
  
  @Schema(name = "promotionCodes", example = "PROMO1", description = "Collection of promotionCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionCodes")
  public List<@Size(min = 1, max = 50)String> getPromotionCodes() {
    return promotionCodes;
  }

  public void setPromotionCodes(List<@Size(min = 1, max = 50)String> promotionCodes) {
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
    PropertySearchRequest propertySearchRequest = (PropertySearchRequest) o;
    return Objects.equals(this.adults, propertySearchRequest.adults) &&
        Objects.equals(this.children, propertySearchRequest.children) &&
        Objects.equals(this.childrenAges, propertySearchRequest.childrenAges) &&
        Objects.equals(this.numberOfUnits, propertySearchRequest.numberOfUnits) &&
        Objects.equals(this.ratePlanCodeMatchOnly, propertySearchRequest.ratePlanCodeMatchOnly) &&
        Objects.equals(this.alternateOffers, propertySearchRequest.alternateOffers) &&
        Objects.equals(this.commissionableStatus, propertySearchRequest.commissionableStatus) &&
        Objects.equals(this.minRate, propertySearchRequest.minRate) &&
        Objects.equals(this.maxRate, propertySearchRequest.maxRate) &&
        Objects.equals(this.hotelCodes, propertySearchRequest.hotelCodes) &&
        Objects.equals(this.arrivalDate, propertySearchRequest.arrivalDate) &&
        Objects.equals(this.departureDate, propertySearchRequest.departureDate) &&
        Objects.equals(this.rateMode, propertySearchRequest.rateMode) &&
        Objects.equals(this.ratePlans, propertySearchRequest.ratePlans) &&
        Objects.equals(this.availableOnly, propertySearchRequest.availableOnly) &&
        Objects.equals(this.promotionCodes, propertySearchRequest.promotionCodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, children, childrenAges, numberOfUnits, ratePlanCodeMatchOnly, alternateOffers, commissionableStatus, minRate, maxRate, hotelCodes, arrivalDate, departureDate, rateMode, ratePlans, availableOnly, promotionCodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertySearchRequest {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    childrenAges: ").append(toIndentedString(childrenAges)).append("\n");
    sb.append("    numberOfUnits: ").append(toIndentedString(numberOfUnits)).append("\n");
    sb.append("    ratePlanCodeMatchOnly: ").append(toIndentedString(ratePlanCodeMatchOnly)).append("\n");
    sb.append("    alternateOffers: ").append(toIndentedString(alternateOffers)).append("\n");
    sb.append("    commissionableStatus: ").append(toIndentedString(commissionableStatus)).append("\n");
    sb.append("    minRate: ").append(toIndentedString(minRate)).append("\n");
    sb.append("    maxRate: ").append(toIndentedString(maxRate)).append("\n");
    sb.append("    hotelCodes: ").append(toIndentedString(hotelCodes)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    rateMode: ").append(toIndentedString(rateMode)).append("\n");
    sb.append("    ratePlans: ").append(toIndentedString(ratePlans)).append("\n");
    sb.append("    availableOnly: ").append(toIndentedString(availableOnly)).append("\n");
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

