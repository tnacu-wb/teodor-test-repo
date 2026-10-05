package uk.co.whitbread.hotel.ocd.adapter.generated.models;

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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRateMode;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Offer Details Request
 */

@Schema(name = "OfferDetailsRequest", description = "Offer Details Request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferDetailsRequest {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate arrivalDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate departureDate;

  private Integer adults = 1;

  private Integer children = 0;

  @Valid
  private List<@Min(0) @Max(18)Integer> childrenAges = new ArrayList<>();

  private @Nullable OfferRateMode rateMode;

  private @Nullable String roomType;

  private @Nullable String ratePlanCode;

  private @Nullable String bookingCode;

  private @Nullable String accessCode;

  private Integer numberOfUnits = 1;

  private Boolean includeAmenities = false;

  @Valid
  private List<@Size(min = 1, max = 50)String> promotionCodes = new ArrayList<>();

  private @Nullable String blockCode;

  public OfferDetailsRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OfferDetailsRequest(LocalDate arrivalDate, LocalDate departureDate) {
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
  }

  public OfferDetailsRequest arrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Arrival/Check-in Date
   * @return arrivalDate
   */
  @NotNull @Valid 
  @Schema(name = "arrivalDate", example = "2021-06-01", description = "Arrival/Check-in Date", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrivalDate")
  public LocalDate getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public OfferDetailsRequest departureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Departure/Check-out Date
   * @return departureDate
   */
  @NotNull @Valid 
  @Schema(name = "departureDate", example = "2021-06-07", description = "Departure/Check-out Date", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departureDate")
  public LocalDate getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
  }

  public OfferDetailsRequest adults(Integer adults) {
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

  public OfferDetailsRequest children(Integer children) {
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

  public OfferDetailsRequest childrenAges(List<@Min(0) @Max(18)Integer> childrenAges) {
    this.childrenAges = childrenAges;
    return this;
  }

  public OfferDetailsRequest addChildrenAgesItem(Integer childrenAgesItem) {
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

  public OfferDetailsRequest rateMode(OfferRateMode rateMode) {
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

  public OfferDetailsRequest roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * bookingCode or both ratePlanCode and roomTypeCode have to be provided
   * @return roomType
   */
  @Size(min = 1, max = 50) 
  @Schema(name = "roomType", example = "", description = "bookingCode or both ratePlanCode and roomTypeCode have to be provided", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public OfferDetailsRequest ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * bookingCode or both ratePlanCode and roomTypeCode have to be provided
   * @return ratePlanCode
   */
  @Size(min = 1, max = 50) 
  @Schema(name = "ratePlanCode", description = "bookingCode or both ratePlanCode and roomTypeCode have to be provided", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public OfferDetailsRequest bookingCode(String bookingCode) {
    this.bookingCode = bookingCode;
    return this;
  }

  /**
   * BookingCode is the concatenation of channel roomTypeCode and channel rate plan code at the offer level
   * @return bookingCode
   */
  @Size(min = 1, max = 100) 
  @Schema(name = "bookingCode", example = "XA1KXDAILY", description = "BookingCode is the concatenation of channel roomTypeCode and channel rate plan code at the offer level", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingCode")
  public String getBookingCode() {
    return bookingCode;
  }

  public void setBookingCode(String bookingCode) {
    this.bookingCode = bookingCode;
  }

  public OfferDetailsRequest accessCode(String accessCode) {
    this.accessCode = accessCode;
    return this;
  }

  /**
   * Access code
   * @return accessCode
   */
  @Size(min = 1, max = 50) 
  @Schema(name = "accessCode", description = "Access code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessCode")
  public String getAccessCode() {
    return accessCode;
  }

  public void setAccessCode(String accessCode) {
    this.accessCode = accessCode;
  }

  public OfferDetailsRequest numberOfUnits(Integer numberOfUnits) {
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

  public OfferDetailsRequest includeAmenities(Boolean includeAmenities) {
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

  public OfferDetailsRequest promotionCodes(List<@Size(min = 1, max = 50)String> promotionCodes) {
    this.promotionCodes = promotionCodes;
    return this;
  }

  public OfferDetailsRequest addPromotionCodesItem(String promotionCodesItem) {
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

  public OfferDetailsRequest blockCode(String blockCode) {
    this.blockCode = blockCode;
    return this;
  }

  /**
   * A code to retrieve price and availability from OPERA Cloud business block. Search parameters ratePlanCodes, accessCode, ratePlanType, ratePlanCodeMatchOnly will be ignored when blockCode is present. <p>Note: blockCode search parameter is not supported if PMS is connected via OXI interface. </p>
   * @return blockCode
   */
  @Size(min = 1, max = 20) 
  @Schema(name = "blockCode", example = "ABCEVENT0516", description = "A code to retrieve price and availability from OPERA Cloud business block. Search parameters ratePlanCodes, accessCode, ratePlanType, ratePlanCodeMatchOnly will be ignored when blockCode is present. <p>Note: blockCode search parameter is not supported if PMS is connected via OXI interface. </p>", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("blockCode")
  public String getBlockCode() {
    return blockCode;
  }

  public void setBlockCode(String blockCode) {
    this.blockCode = blockCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferDetailsRequest offerDetailsRequest = (OfferDetailsRequest) o;
    return Objects.equals(this.arrivalDate, offerDetailsRequest.arrivalDate) &&
        Objects.equals(this.departureDate, offerDetailsRequest.departureDate) &&
        Objects.equals(this.adults, offerDetailsRequest.adults) &&
        Objects.equals(this.children, offerDetailsRequest.children) &&
        Objects.equals(this.childrenAges, offerDetailsRequest.childrenAges) &&
        Objects.equals(this.rateMode, offerDetailsRequest.rateMode) &&
        Objects.equals(this.roomType, offerDetailsRequest.roomType) &&
        Objects.equals(this.ratePlanCode, offerDetailsRequest.ratePlanCode) &&
        Objects.equals(this.bookingCode, offerDetailsRequest.bookingCode) &&
        Objects.equals(this.accessCode, offerDetailsRequest.accessCode) &&
        Objects.equals(this.numberOfUnits, offerDetailsRequest.numberOfUnits) &&
        Objects.equals(this.includeAmenities, offerDetailsRequest.includeAmenities) &&
        Objects.equals(this.promotionCodes, offerDetailsRequest.promotionCodes) &&
        Objects.equals(this.blockCode, offerDetailsRequest.blockCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, departureDate, adults, children, childrenAges, rateMode, roomType, ratePlanCode, bookingCode, accessCode, numberOfUnits, includeAmenities, promotionCodes, blockCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferDetailsRequest {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    childrenAges: ").append(toIndentedString(childrenAges)).append("\n");
    sb.append("    rateMode: ").append(toIndentedString(rateMode)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    bookingCode: ").append(toIndentedString(bookingCode)).append("\n");
    sb.append("    accessCode: ").append(toIndentedString(accessCode)).append("\n");
    sb.append("    numberOfUnits: ").append(toIndentedString(numberOfUnits)).append("\n");
    sb.append("    includeAmenities: ").append(toIndentedString(includeAmenities)).append("\n");
    sb.append("    promotionCodes: ").append(toIndentedString(promotionCodes)).append("\n");
    sb.append("    blockCode: ").append(toIndentedString(blockCode)).append("\n");
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

