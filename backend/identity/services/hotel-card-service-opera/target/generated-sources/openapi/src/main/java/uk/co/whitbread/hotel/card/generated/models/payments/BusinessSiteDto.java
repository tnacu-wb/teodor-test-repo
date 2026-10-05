package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Information on the site the booking is for. A site can be a hotel, restaurant or another entity run by Whitbread.
 */

@Schema(name = "BusinessSite", description = "Information on the site the booking is for. A site can be a hotel, restaurant or another entity run by Whitbread.")
@JsonTypeName("BusinessSite")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessSiteDto {

  @Valid
  private List<String> additionalServices = new ArrayList<>();

  private @Nullable String country;

  private String identifier;

  private @Nullable String location;

  private @Nullable String name;

  /**
   * Type of business site.
   */
  public enum TypeEnum {
    HOTEL("HOTEL"),
    
    RESTAURANT("RESTAURANT"),
    
    OTHER("OTHER");

    private String value;

    TypeEnum(String value) {
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
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TypeEnum type;

  public BusinessSiteDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BusinessSiteDto(String identifier, TypeEnum type) {
    this.identifier = identifier;
    this.type = type;
  }

  public BusinessSiteDto additionalServices(List<String> additionalServices) {
    this.additionalServices = additionalServices;
    return this;
  }

  public BusinessSiteDto addAdditionalServicesItem(String additionalServicesItem) {
    if (this.additionalServices == null) {
      this.additionalServices = new ArrayList<>();
    }
    this.additionalServices.add(additionalServicesItem);
    return this;
  }

  /**
   * Get additionalServices
   * @return additionalServices
   */
  
  @Schema(name = "additionalServices", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("additionalServices")
  public List<String> getAdditionalServices() {
    return additionalServices;
  }

  public void setAdditionalServices(List<String> additionalServices) {
    this.additionalServices = additionalServices;
  }

  public BusinessSiteDto country(String country) {
    this.country = country;
    return this;
  }

  /**
   * The country that the business site is in.
   * @return country
   */
  
  @Schema(name = "country", example = "GB", description = "The country that the business site is in.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("country")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public BusinessSiteDto identifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  /**
   * The unique site identifier.
   * @return identifier
   */
  @NotNull 
  @Schema(name = "identifier", example = "LONHOL", description = "The unique site identifier.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("identifier")
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public BusinessSiteDto location(String location) {
    this.location = location;
    return this;
  }

  /**
   * The city/town that the business site is in.
   * @return location
   */
  
  @Schema(name = "location", example = "London", description = "The city/town that the business site is in.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("location")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public BusinessSiteDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the site.
   * @return name
   */
  
  @Schema(name = "name", example = "London Holborn Premier Inn", description = "The name of the site.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BusinessSiteDto type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Type of business site.
   * @return type
   */
  @NotNull 
  @Schema(name = "type", example = "HOTEL", description = "Type of business site.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessSiteDto businessSite = (BusinessSiteDto) o;
    return Objects.equals(this.additionalServices, businessSite.additionalServices) &&
        Objects.equals(this.country, businessSite.country) &&
        Objects.equals(this.identifier, businessSite.identifier) &&
        Objects.equals(this.location, businessSite.location) &&
        Objects.equals(this.name, businessSite.name) &&
        Objects.equals(this.type, businessSite.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalServices, country, identifier, location, name, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessSiteDto {\n");
    sb.append("    additionalServices: ").append(toIndentedString(additionalServices)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

