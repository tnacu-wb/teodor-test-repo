package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * The point of interest for guests to get to the hotel.
 */

@Schema(name = "OfferPointOfInterest", description = "The point of interest for guests to get to the hotel.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferPointOfInterest {

  private @Nullable String name;

  /**
   * The point of interest type available near the property.
   */
  public enum PointOfInterestTypeEnum {
    AIRPORT("AIRPORT"),
    
    ATTRACTIONS("ATTRACTIONS");

    private String value;

    PointOfInterestTypeEnum(String value) {
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
    public static PointOfInterestTypeEnum fromValue(String value) {
      for (PointOfInterestTypeEnum b : PointOfInterestTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable PointOfInterestTypeEnum pointOfInterestType;

  private @Nullable String description;

  private @Nullable String airportCode;

  private @Nullable Double distance;

  private @Nullable String distanceUnit;

  /**
   * Relative direction from the point of interest to the property. North, West, South, East N: North, W: West, S: South, E: East, NW: North West, NE: North East, SW: South West, SE: South East 
   */
  public enum AttractionDirectionEnum {
    N("N"),
    
    W("W"),
    
    S("S"),
    
    E("E"),
    
    NW("NW"),
    
    NE("NE"),
    
    SW("SW"),
    
    SE("SE");

    private String value;

    AttractionDirectionEnum(String value) {
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
    public static AttractionDirectionEnum fromValue(String value) {
      for (AttractionDirectionEnum b : AttractionDirectionEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable AttractionDirectionEnum attractionDirection;

  @Valid
  private List<String> transportation = new ArrayList<>();

  private @Nullable String directionDescription;

  private @Nullable Double drivingTime;

  public OfferPointOfInterest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the point of interest.
   * @return name
   */
  
  @Schema(name = "name", example = "Dallas airport, Park", description = "The name of the point of interest.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OfferPointOfInterest pointOfInterestType(PointOfInterestTypeEnum pointOfInterestType) {
    this.pointOfInterestType = pointOfInterestType;
    return this;
  }

  /**
   * The point of interest type available near the property.
   * @return pointOfInterestType
   */
  
  @Schema(name = "pointOfInterestType", example = "AIRPORT", description = "The point of interest type available near the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pointOfInterestType")
  public PointOfInterestTypeEnum getPointOfInterestType() {
    return pointOfInterestType;
  }

  public void setPointOfInterestType(PointOfInterestTypeEnum pointOfInterestType) {
    this.pointOfInterestType = pointOfInterestType;
  }

  public OfferPointOfInterest description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description for the point of interest.
   * @return description
   */
  
  @Schema(name = "description", description = "Description for the point of interest.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OfferPointOfInterest airportCode(String airportCode) {
    this.airportCode = airportCode;
    return this;
  }

  /**
   * The unique 3 letter IATA code to identify the airport near the property.
   * @return airportCode
   */
  
  @Schema(name = "airportCode", example = "DAL", description = "The unique 3 letter IATA code to identify the airport near the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("airportCode")
  public String getAirportCode() {
    return airportCode;
  }

  public void setAirportCode(String airportCode) {
    this.airportCode = airportCode;
  }

  public OfferPointOfInterest distance(Double distance) {
    this.distance = distance;
    return this;
  }

  /**
   * Distance  from the point of interest to the property.
   * @return distance
   */
  
  @Schema(name = "distance", example = "6.0", description = "Distance  from the point of interest to the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distance")
  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public OfferPointOfInterest distanceUnit(String distanceUnit) {
    this.distanceUnit = distanceUnit;
    return this;
  }

  /**
   * Distance measurement unit for the distance from  the point of interest to the property, or from the property to the point of interest.
   * @return distanceUnit
   */
  
  @Schema(name = "distanceUnit", example = "", description = "Distance measurement unit for the distance from  the point of interest to the property, or from the property to the point of interest.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distanceUnit")
  public String getDistanceUnit() {
    return distanceUnit;
  }

  public void setDistanceUnit(String distanceUnit) {
    this.distanceUnit = distanceUnit;
  }

  public OfferPointOfInterest attractionDirection(AttractionDirectionEnum attractionDirection) {
    this.attractionDirection = attractionDirection;
    return this;
  }

  /**
   * Relative direction from the point of interest to the property. North, West, South, East N: North, W: West, S: South, E: East, NW: North West, NE: North East, SW: South West, SE: South East 
   * @return attractionDirection
   */
  
  @Schema(name = "attractionDirection", example = "N", description = "Relative direction from the point of interest to the property. North, West, South, East N: North, W: West, S: South, E: East, NW: North West, NE: North East, SW: South West, SE: South East ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("attractionDirection")
  public AttractionDirectionEnum getAttractionDirection() {
    return attractionDirection;
  }

  public void setAttractionDirection(AttractionDirectionEnum attractionDirection) {
    this.attractionDirection = attractionDirection;
  }

  public OfferPointOfInterest transportation(List<String> transportation) {
    this.transportation = transportation;
    return this;
  }

  public OfferPointOfInterest addTransportationItem(String transportationItem) {
    if (this.transportation == null) {
      this.transportation = new ArrayList<>();
    }
    this.transportation.add(transportationItem);
    return this;
  }

  /**
   * List of transportation types available from  the point of interest to the property, or from the property to the point of interest.
   * @return transportation
   */
  
  @Schema(name = "transportation", example = "[Taxi, Shuttle]", description = "List of transportation types available from  the point of interest to the property, or from the property to the point of interest.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transportation")
  public List<String> getTransportation() {
    return transportation;
  }

  public void setTransportation(List<String> transportation) {
    this.transportation = transportation;
  }

  public OfferPointOfInterest directionDescription(String directionDescription) {
    this.directionDescription = directionDescription;
    return this;
  }

  /**
   * Description on how to get from the point of interest to the property, or from the property to the point of interest.
   * @return directionDescription
   */
  
  @Schema(name = "directionDescription", example = "Free text to describe route from property to attraction", description = "Description on how to get from the point of interest to the property, or from the property to the point of interest.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("directionDescription")
  public String getDirectionDescription() {
    return directionDescription;
  }

  public void setDirectionDescription(String directionDescription) {
    this.directionDescription = directionDescription;
  }

  public OfferPointOfInterest drivingTime(Double drivingTime) {
    this.drivingTime = drivingTime;
    return this;
  }

  /**
   * The driving time it will take to get from point of interest to the property.
   * @return drivingTime
   */
  
  @Schema(name = "drivingTime", example = "3.5", description = "The driving time it will take to get from point of interest to the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("drivingTime")
  public Double getDrivingTime() {
    return drivingTime;
  }

  public void setDrivingTime(Double drivingTime) {
    this.drivingTime = drivingTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferPointOfInterest offerPointOfInterest = (OfferPointOfInterest) o;
    return Objects.equals(this.name, offerPointOfInterest.name) &&
        Objects.equals(this.pointOfInterestType, offerPointOfInterest.pointOfInterestType) &&
        Objects.equals(this.description, offerPointOfInterest.description) &&
        Objects.equals(this.airportCode, offerPointOfInterest.airportCode) &&
        Objects.equals(this.distance, offerPointOfInterest.distance) &&
        Objects.equals(this.distanceUnit, offerPointOfInterest.distanceUnit) &&
        Objects.equals(this.attractionDirection, offerPointOfInterest.attractionDirection) &&
        Objects.equals(this.transportation, offerPointOfInterest.transportation) &&
        Objects.equals(this.directionDescription, offerPointOfInterest.directionDescription) &&
        Objects.equals(this.drivingTime, offerPointOfInterest.drivingTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, pointOfInterestType, description, airportCode, distance, distanceUnit, attractionDirection, transportation, directionDescription, drivingTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferPointOfInterest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    pointOfInterestType: ").append(toIndentedString(pointOfInterestType)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    airportCode: ").append(toIndentedString(airportCode)).append("\n");
    sb.append("    distance: ").append(toIndentedString(distance)).append("\n");
    sb.append("    distanceUnit: ").append(toIndentedString(distanceUnit)).append("\n");
    sb.append("    attractionDirection: ").append(toIndentedString(attractionDirection)).append("\n");
    sb.append("    transportation: ").append(toIndentedString(transportation)).append("\n");
    sb.append("    directionDescription: ").append(toIndentedString(directionDescription)).append("\n");
    sb.append("    drivingTime: ").append(toIndentedString(drivingTime)).append("\n");
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

