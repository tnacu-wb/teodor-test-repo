package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Address;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.AlternatePropertyDistance;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Communications;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Direction;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.GeneralInformation;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Location;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferPointOfInterest;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertyOffersHotelAmenity;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Transportation;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Property information
 */

@Schema(name = "OfferDetailsPropertyInfo", description = "Property information")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferDetailsPropertyInfo {

  private @Nullable String hotelCode;

  private @Nullable String hotelName;

  private @Nullable String chainCode;

  private Boolean isAlternate = false;

  private @Nullable Address address;

  private @Nullable AlternatePropertyDistance distance;

  @Valid
  private List<@Valid PropertyOffersHotelAmenity> propertyAmenities = new ArrayList<>();

  @Valid
  private List<@Valid OfferPointOfInterest> pointOfInterest = new ArrayList<>();

  private @Nullable String marketingMessage;

  private @Nullable GeneralInformation generalInformation;

  private @Nullable Communications communications;

  @Valid
  private List<@Valid Transportation> transportations = new ArrayList<>();

  private @Nullable Direction direction;

  private @Nullable Location location;

  public OfferDetailsPropertyInfo hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * A unique identifier for the property.
   * @return hotelCode
   */
  
  @Schema(name = "hotelCode", example = "XUSXXYY99", description = "A unique identifier for the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public OfferDetailsPropertyInfo hotelName(String hotelName) {
    this.hotelName = hotelName;
    return this;
  }

  /**
   * Name of the property.
   * @return hotelName
   */
  
  @Schema(name = "hotelName", example = "Resort1", description = "Name of the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelName")
  public String getHotelName() {
    return hotelName;
  }

  public void setHotelName(String hotelName) {
    this.hotelName = hotelName;
  }

  public OfferDetailsPropertyInfo chainCode(String chainCode) {
    this.chainCode = chainCode;
    return this;
  }

  /**
   * If the property is part of the chain, the associated chain code.
   * @return chainCode
   */
  
  @Schema(name = "chainCode", example = "CHAIN1", description = "If the property is part of the chain, the associated chain code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("chainCode")
  public String getChainCode() {
    return chainCode;
  }

  public void setChainCode(String chainCode) {
    this.chainCode = chainCode;
  }

  public OfferDetailsPropertyInfo isAlternate(Boolean isAlternate) {
    this.isAlternate = isAlternate;
    return this;
  }

  /**
   * When true indicates the property returned is an alternate property.
   * @return isAlternate
   */
  
  @Schema(name = "isAlternate", example = "true", description = "When true indicates the property returned is an alternate property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isAlternate")
  public Boolean getIsAlternate() {
    return isAlternate;
  }

  public void setIsAlternate(Boolean isAlternate) {
    this.isAlternate = isAlternate;
  }

  public OfferDetailsPropertyInfo address(Address address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @Valid 
  @Schema(name = "address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("address")
  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public OfferDetailsPropertyInfo distance(AlternatePropertyDistance distance) {
    this.distance = distance;
    return this;
  }

  /**
   * Get distance
   * @return distance
   */
  @Valid 
  @Schema(name = "distance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distance")
  public AlternatePropertyDistance getDistance() {
    return distance;
  }

  public void setDistance(AlternatePropertyDistance distance) {
    this.distance = distance;
  }

  public OfferDetailsPropertyInfo propertyAmenities(List<@Valid PropertyOffersHotelAmenity> propertyAmenities) {
    this.propertyAmenities = propertyAmenities;
    return this;
  }

  public OfferDetailsPropertyInfo addPropertyAmenitiesItem(PropertyOffersHotelAmenity propertyAmenitiesItem) {
    if (this.propertyAmenities == null) {
      this.propertyAmenities = new ArrayList<>();
    }
    this.propertyAmenities.add(propertyAmenitiesItem);
    return this;
  }

  /**
   * List of amenities offered at the property.
   * @return propertyAmenities
   */
  @Valid 
  @Schema(name = "propertyAmenities", description = "List of amenities offered at the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("propertyAmenities")
  public List<@Valid PropertyOffersHotelAmenity> getPropertyAmenities() {
    return propertyAmenities;
  }

  public void setPropertyAmenities(List<@Valid PropertyOffersHotelAmenity> propertyAmenities) {
    this.propertyAmenities = propertyAmenities;
  }

  public OfferDetailsPropertyInfo pointOfInterest(List<@Valid OfferPointOfInterest> pointOfInterest) {
    this.pointOfInterest = pointOfInterest;
    return this;
  }

  public OfferDetailsPropertyInfo addPointOfInterestItem(OfferPointOfInterest pointOfInterestItem) {
    if (this.pointOfInterest == null) {
      this.pointOfInterest = new ArrayList<>();
    }
    this.pointOfInterest.add(pointOfInterestItem);
    return this;
  }

  /**
   * List of locations near the property.
   * @return pointOfInterest
   */
  @Valid 
  @Schema(name = "pointOfInterest", description = "List of locations near the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pointOfInterest")
  public List<@Valid OfferPointOfInterest> getPointOfInterest() {
    return pointOfInterest;
  }

  public void setPointOfInterest(List<@Valid OfferPointOfInterest> pointOfInterest) {
    this.pointOfInterest = pointOfInterest;
  }

  public OfferDetailsPropertyInfo marketingMessage(String marketingMessage) {
    this.marketingMessage = marketingMessage;
    return this;
  }

  /**
   * Marketing text information for the property.
   * @return marketingMessage
   */
  
  @Schema(name = "marketingMessage", example = "Thank you for choosing our property.", description = "Marketing text information for the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("marketingMessage")
  public String getMarketingMessage() {
    return marketingMessage;
  }

  public void setMarketingMessage(String marketingMessage) {
    this.marketingMessage = marketingMessage;
  }

  public OfferDetailsPropertyInfo generalInformation(GeneralInformation generalInformation) {
    this.generalInformation = generalInformation;
    return this;
  }

  /**
   * Get generalInformation
   * @return generalInformation
   */
  @Valid 
  @Schema(name = "generalInformation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("generalInformation")
  public GeneralInformation getGeneralInformation() {
    return generalInformation;
  }

  public void setGeneralInformation(GeneralInformation generalInformation) {
    this.generalInformation = generalInformation;
  }

  public OfferDetailsPropertyInfo communications(Communications communications) {
    this.communications = communications;
    return this;
  }

  /**
   * Get communications
   * @return communications
   */
  @Valid 
  @Schema(name = "communications", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("communications")
  public Communications getCommunications() {
    return communications;
  }

  public void setCommunications(Communications communications) {
    this.communications = communications;
  }

  public OfferDetailsPropertyInfo transportations(List<@Valid Transportation> transportations) {
    this.transportations = transportations;
    return this;
  }

  public OfferDetailsPropertyInfo addTransportationsItem(Transportation transportationsItem) {
    if (this.transportations == null) {
      this.transportations = new ArrayList<>();
    }
    this.transportations.add(transportationsItem);
    return this;
  }

  /**
   * List of transportation services that are available to and from the property.
   * @return transportations
   */
  @Valid 
  @Schema(name = "transportations", description = "List of transportation services that are available to and from the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transportations")
  public List<@Valid Transportation> getTransportations() {
    return transportations;
  }

  public void setTransportations(List<@Valid Transportation> transportations) {
    this.transportations = transportations;
  }

  public OfferDetailsPropertyInfo direction(Direction direction) {
    this.direction = direction;
    return this;
  }

  /**
   * Get direction
   * @return direction
   */
  @Valid 
  @Schema(name = "direction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("direction")
  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public OfferDetailsPropertyInfo location(Location location) {
    this.location = location;
    return this;
  }

  /**
   * Get location
   * @return location
   */
  @Valid 
  @Schema(name = "location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("location")
  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferDetailsPropertyInfo offerDetailsPropertyInfo = (OfferDetailsPropertyInfo) o;
    return Objects.equals(this.hotelCode, offerDetailsPropertyInfo.hotelCode) &&
        Objects.equals(this.hotelName, offerDetailsPropertyInfo.hotelName) &&
        Objects.equals(this.chainCode, offerDetailsPropertyInfo.chainCode) &&
        Objects.equals(this.isAlternate, offerDetailsPropertyInfo.isAlternate) &&
        Objects.equals(this.address, offerDetailsPropertyInfo.address) &&
        Objects.equals(this.distance, offerDetailsPropertyInfo.distance) &&
        Objects.equals(this.propertyAmenities, offerDetailsPropertyInfo.propertyAmenities) &&
        Objects.equals(this.pointOfInterest, offerDetailsPropertyInfo.pointOfInterest) &&
        Objects.equals(this.marketingMessage, offerDetailsPropertyInfo.marketingMessage) &&
        Objects.equals(this.generalInformation, offerDetailsPropertyInfo.generalInformation) &&
        Objects.equals(this.communications, offerDetailsPropertyInfo.communications) &&
        Objects.equals(this.transportations, offerDetailsPropertyInfo.transportations) &&
        Objects.equals(this.direction, offerDetailsPropertyInfo.direction) &&
        Objects.equals(this.location, offerDetailsPropertyInfo.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelCode, hotelName, chainCode, isAlternate, address, distance, propertyAmenities, pointOfInterest, marketingMessage, generalInformation, communications, transportations, direction, location);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferDetailsPropertyInfo {\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    chainCode: ").append(toIndentedString(chainCode)).append("\n");
    sb.append("    isAlternate: ").append(toIndentedString(isAlternate)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    distance: ").append(toIndentedString(distance)).append("\n");
    sb.append("    propertyAmenities: ").append(toIndentedString(propertyAmenities)).append("\n");
    sb.append("    pointOfInterest: ").append(toIndentedString(pointOfInterest)).append("\n");
    sb.append("    marketingMessage: ").append(toIndentedString(marketingMessage)).append("\n");
    sb.append("    generalInformation: ").append(toIndentedString(generalInformation)).append("\n");
    sb.append("    communications: ").append(toIndentedString(communications)).append("\n");
    sb.append("    transportations: ").append(toIndentedString(transportations)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
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

