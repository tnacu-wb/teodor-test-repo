package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * CityTaxDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CityTaxDto {

  private @Nullable String cityTaxWebUrl;

  private @Nullable Boolean isCityTaxBusinessHotel;

  private @Nullable Boolean isCityTaxHotel;

  public CityTaxDto cityTaxWebUrl(String cityTaxWebUrl) {
    this.cityTaxWebUrl = cityTaxWebUrl;
    return this;
  }

  /**
   * Get cityTaxWebUrl
   * @return cityTaxWebUrl
   */
  
  @Schema(name = "cityTaxWebUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cityTaxWebUrl")
  public String getCityTaxWebUrl() {
    return cityTaxWebUrl;
  }

  public void setCityTaxWebUrl(String cityTaxWebUrl) {
    this.cityTaxWebUrl = cityTaxWebUrl;
  }

  public CityTaxDto isCityTaxBusinessHotel(Boolean isCityTaxBusinessHotel) {
    this.isCityTaxBusinessHotel = isCityTaxBusinessHotel;
    return this;
  }

  /**
   * Get isCityTaxBusinessHotel
   * @return isCityTaxBusinessHotel
   */
  
  @Schema(name = "isCityTaxBusinessHotel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCityTaxBusinessHotel")
  public Boolean getIsCityTaxBusinessHotel() {
    return isCityTaxBusinessHotel;
  }

  public void setIsCityTaxBusinessHotel(Boolean isCityTaxBusinessHotel) {
    this.isCityTaxBusinessHotel = isCityTaxBusinessHotel;
  }

  public CityTaxDto isCityTaxHotel(Boolean isCityTaxHotel) {
    this.isCityTaxHotel = isCityTaxHotel;
    return this;
  }

  /**
   * Get isCityTaxHotel
   * @return isCityTaxHotel
   */
  
  @Schema(name = "isCityTaxHotel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCityTaxHotel")
  public Boolean getIsCityTaxHotel() {
    return isCityTaxHotel;
  }

  public void setIsCityTaxHotel(Boolean isCityTaxHotel) {
    this.isCityTaxHotel = isCityTaxHotel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CityTaxDto cityTaxDto = (CityTaxDto) o;
    return Objects.equals(this.cityTaxWebUrl, cityTaxDto.cityTaxWebUrl) &&
        Objects.equals(this.isCityTaxBusinessHotel, cityTaxDto.isCityTaxBusinessHotel) &&
        Objects.equals(this.isCityTaxHotel, cityTaxDto.isCityTaxHotel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cityTaxWebUrl, isCityTaxBusinessHotel, isCityTaxHotel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CityTaxDto {\n");
    sb.append("    cityTaxWebUrl: ").append(toIndentedString(cityTaxWebUrl)).append("\n");
    sb.append("    isCityTaxBusinessHotel: ").append(toIndentedString(isCityTaxBusinessHotel)).append("\n");
    sb.append("    isCityTaxHotel: ").append(toIndentedString(isCityTaxHotel)).append("\n");
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

