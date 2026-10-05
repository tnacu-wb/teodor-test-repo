package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomCriteria
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomCriteria {

  private Long adults;

  private Long children;

  private Boolean cotRequired;

  /**
   * Gets or Sets hotelBrand
   */
  public enum HotelBrandEnum {
    HUB("HUB"),
    
    PI("PI"),
    
    PID("PID"),
    
    CBT("CBT"),
    
    ZIP("ZIP");

    private String value;

    HotelBrandEnum(String value) {
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
    public static HotelBrandEnum fromValue(String value) {
      for (HotelBrandEnum b : HotelBrandEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable HotelBrandEnum hotelBrand;

  private @Nullable String lettingType;

  private String type;

  public RoomCriteria() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomCriteria(Long adults, Long children, Boolean cotRequired, String type) {
    this.adults = adults;
    this.children = children;
    this.cotRequired = cotRequired;
    this.type = type;
  }

  public RoomCriteria adults(Long adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * minimum: 0
   * @return adults
   */
  @NotNull @Min(0L) 
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adults")
  public Long getAdults() {
    return adults;
  }

  public void setAdults(Long adults) {
    this.adults = adults;
  }

  public RoomCriteria children(Long children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * minimum: 0
   * @return children
   */
  @NotNull @Min(0L) 
  @Schema(name = "children", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("children")
  public Long getChildren() {
    return children;
  }

  public void setChildren(Long children) {
    this.children = children;
  }

  public RoomCriteria cotRequired(Boolean cotRequired) {
    this.cotRequired = cotRequired;
    return this;
  }

  /**
   * Get cotRequired
   * @return cotRequired
   */
  @NotNull 
  @Schema(name = "cotRequired", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("cotRequired")
  public Boolean getCotRequired() {
    return cotRequired;
  }

  public void setCotRequired(Boolean cotRequired) {
    this.cotRequired = cotRequired;
  }

  public RoomCriteria hotelBrand(HotelBrandEnum hotelBrand) {
    this.hotelBrand = hotelBrand;
    return this;
  }

  /**
   * Get hotelBrand
   * @return hotelBrand
   */
  
  @Schema(name = "hotelBrand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelBrand")
  public HotelBrandEnum getHotelBrand() {
    return hotelBrand;
  }

  public void setHotelBrand(HotelBrandEnum hotelBrand) {
    this.hotelBrand = hotelBrand;
  }

  public RoomCriteria lettingType(String lettingType) {
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

  public RoomCriteria type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  @NotNull 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
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
    RoomCriteria roomCriteria = (RoomCriteria) o;
    return Objects.equals(this.adults, roomCriteria.adults) &&
        Objects.equals(this.children, roomCriteria.children) &&
        Objects.equals(this.cotRequired, roomCriteria.cotRequired) &&
        Objects.equals(this.hotelBrand, roomCriteria.hotelBrand) &&
        Objects.equals(this.lettingType, roomCriteria.lettingType) &&
        Objects.equals(this.type, roomCriteria.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, children, cotRequired, hotelBrand, lettingType, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomCriteria {\n");
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

