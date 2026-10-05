package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessSiteDto {

  private String identifier;

  private @Nullable String name;

  private String type;

  private @Nullable String location;

  public BusinessSiteDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BusinessSiteDto(String identifier, String type) {
    this.identifier = identifier;
    this.type = type;
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

  public BusinessSiteDto type(String type) {
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
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessSiteDto businessSite = (BusinessSiteDto) o;
    return Objects.equals(this.identifier, businessSite.identifier) &&
        Objects.equals(this.name, businessSite.name) &&
        Objects.equals(this.type, businessSite.type) &&
        Objects.equals(this.location, businessSite.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, name, type, location);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessSiteDto {\n");
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

