package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

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
 * RoomRequirementsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomRequirementsDto {

  private @Nullable Integer adults;

  private @Nullable Integer children;

  private @Nullable Boolean cotRequired;

  private @Nullable String hotelBrand;

  private @Nullable String lettingType;

  private @Nullable String type;

  public RoomRequirementsDto adults(Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * @return adults
   */
  
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public Integer getAdults() {
    return adults;
  }

  public void setAdults(Integer adults) {
    this.adults = adults;
  }

  public RoomRequirementsDto children(Integer children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * @return children
   */
  
  @Schema(name = "children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public Integer getChildren() {
    return children;
  }

  public void setChildren(Integer children) {
    this.children = children;
  }

  public RoomRequirementsDto cotRequired(Boolean cotRequired) {
    this.cotRequired = cotRequired;
    return this;
  }

  /**
   * Get cotRequired
   * @return cotRequired
   */
  
  @Schema(name = "cotRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cotRequired")
  public Boolean getCotRequired() {
    return cotRequired;
  }

  public void setCotRequired(Boolean cotRequired) {
    this.cotRequired = cotRequired;
  }

  public RoomRequirementsDto hotelBrand(String hotelBrand) {
    this.hotelBrand = hotelBrand;
    return this;
  }

  /**
   * Get hotelBrand
   * @return hotelBrand
   */
  
  @Schema(name = "hotelBrand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelBrand")
  public String getHotelBrand() {
    return hotelBrand;
  }

  public void setHotelBrand(String hotelBrand) {
    this.hotelBrand = hotelBrand;
  }

  public RoomRequirementsDto lettingType(String lettingType) {
    this.lettingType = lettingType;
    return this;
  }

  /**
   * Get lettingType
   * @return lettingType
   */
  
  @Schema(name = "lettingType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lettingType")
  public String getLettingType() {
    return lettingType;
  }

  public void setLettingType(String lettingType) {
    this.lettingType = lettingType;
  }

  public RoomRequirementsDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
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
    RoomRequirementsDto roomRequirementsDto = (RoomRequirementsDto) o;
    return Objects.equals(this.adults, roomRequirementsDto.adults) &&
        Objects.equals(this.children, roomRequirementsDto.children) &&
        Objects.equals(this.cotRequired, roomRequirementsDto.cotRequired) &&
        Objects.equals(this.hotelBrand, roomRequirementsDto.hotelBrand) &&
        Objects.equals(this.lettingType, roomRequirementsDto.lettingType) &&
        Objects.equals(this.type, roomRequirementsDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, children, cotRequired, hotelBrand, lettingType, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomRequirementsDto {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    cotRequired: ").append(toIndentedString(cotRequired)).append("\n");
    sb.append("    hotelBrand: ").append(toIndentedString(hotelBrand)).append("\n");
    sb.append("    lettingType: ").append(toIndentedString(lettingType)).append("\n");
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

