package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomRequirementsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class RoomRequirementsDto {

  private @Nullable Integer adults;

  private @Nullable Integer children;

  private @Nullable Boolean cotRequired;

  private @Nullable String hotelBrand;

  private @Nullable String lettingType;

  private @Nullable String type;

  public RoomRequirementsDto adults(@Nullable Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * @return adults
   */
  
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public @Nullable Integer getAdults() {
    return adults;
  }

  public void setAdults(@Nullable Integer adults) {
    this.adults = adults;
  }

  public RoomRequirementsDto children(@Nullable Integer children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * @return children
   */
  
  @Schema(name = "children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public @Nullable Integer getChildren() {
    return children;
  }

  public void setChildren(@Nullable Integer children) {
    this.children = children;
  }

  public RoomRequirementsDto cotRequired(@Nullable Boolean cotRequired) {
    this.cotRequired = cotRequired;
    return this;
  }

  /**
   * Get cotRequired
   * @return cotRequired
   */
  
  @Schema(name = "cotRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cotRequired")
  public @Nullable Boolean getCotRequired() {
    return cotRequired;
  }

  public void setCotRequired(@Nullable Boolean cotRequired) {
    this.cotRequired = cotRequired;
  }

  public RoomRequirementsDto hotelBrand(@Nullable String hotelBrand) {
    this.hotelBrand = hotelBrand;
    return this;
  }

  /**
   * Get hotelBrand
   * @return hotelBrand
   */
  
  @Schema(name = "hotelBrand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelBrand")
  public @Nullable String getHotelBrand() {
    return hotelBrand;
  }

  public void setHotelBrand(@Nullable String hotelBrand) {
    this.hotelBrand = hotelBrand;
  }

  public RoomRequirementsDto lettingType(@Nullable String lettingType) {
    this.lettingType = lettingType;
    return this;
  }

  /**
   * Get lettingType
   * @return lettingType
   */
  
  @Schema(name = "lettingType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lettingType")
  public @Nullable String getLettingType() {
    return lettingType;
  }

  public void setLettingType(@Nullable String lettingType) {
    this.lettingType = lettingType;
  }

  public RoomRequirementsDto type(@Nullable String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public @Nullable String getType() {
    return type;
  }

  public void setType(@Nullable String type) {
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

