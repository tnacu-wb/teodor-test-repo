package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
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

@Schema(name = "PropertySearchPropertyInfo", description = "Property information")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertySearchPropertyInfo {

  private @Nullable String hotelCode;

  private @Nullable String hotelName;

  private @Nullable String chainCode;

  private Boolean isAlternate = false;

  public PropertySearchPropertyInfo hotelCode(String hotelCode) {
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

  public PropertySearchPropertyInfo hotelName(String hotelName) {
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

  public PropertySearchPropertyInfo chainCode(String chainCode) {
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

  public PropertySearchPropertyInfo isAlternate(Boolean isAlternate) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertySearchPropertyInfo propertySearchPropertyInfo = (PropertySearchPropertyInfo) o;
    return Objects.equals(this.hotelCode, propertySearchPropertyInfo.hotelCode) &&
        Objects.equals(this.hotelName, propertySearchPropertyInfo.hotelName) &&
        Objects.equals(this.chainCode, propertySearchPropertyInfo.chainCode) &&
        Objects.equals(this.isAlternate, propertySearchPropertyInfo.isAlternate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelCode, hotelName, chainCode, isAlternate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertySearchPropertyInfo {\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    chainCode: ").append(toIndentedString(chainCode)).append("\n");
    sb.append("    isAlternate: ").append(toIndentedString(isAlternate)).append("\n");
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

