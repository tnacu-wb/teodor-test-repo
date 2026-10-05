package uk.co.whitbread.basket.generated.models.ohip;

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
 * RestaurantDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RestaurantDto {

  private @Nullable String logoUrl;

  private @Nullable Boolean noMealsFound;

  private @Nullable Boolean restaurantNotFound;

  public RestaurantDto logoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
    return this;
  }

  /**
   * Get logoUrl
   * @return logoUrl
   */
  
  @Schema(name = "logoUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logoUrl")
  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public RestaurantDto noMealsFound(Boolean noMealsFound) {
    this.noMealsFound = noMealsFound;
    return this;
  }

  /**
   * Get noMealsFound
   * @return noMealsFound
   */
  
  @Schema(name = "noMealsFound", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noMealsFound")
  public Boolean getNoMealsFound() {
    return noMealsFound;
  }

  public void setNoMealsFound(Boolean noMealsFound) {
    this.noMealsFound = noMealsFound;
  }

  public RestaurantDto restaurantNotFound(Boolean restaurantNotFound) {
    this.restaurantNotFound = restaurantNotFound;
    return this;
  }

  /**
   * Get restaurantNotFound
   * @return restaurantNotFound
   */
  
  @Schema(name = "restaurantNotFound", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restaurantNotFound")
  public Boolean getRestaurantNotFound() {
    return restaurantNotFound;
  }

  public void setRestaurantNotFound(Boolean restaurantNotFound) {
    this.restaurantNotFound = restaurantNotFound;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RestaurantDto restaurantDto = (RestaurantDto) o;
    return Objects.equals(this.logoUrl, restaurantDto.logoUrl) &&
        Objects.equals(this.noMealsFound, restaurantDto.noMealsFound) &&
        Objects.equals(this.restaurantNotFound, restaurantDto.restaurantNotFound);
  }

  @Override
  public int hashCode() {
    return Objects.hash(logoUrl, noMealsFound, restaurantNotFound);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RestaurantDto {\n");
    sb.append("    logoUrl: ").append(toIndentedString(logoUrl)).append("\n");
    sb.append("    noMealsFound: ").append(toIndentedString(noMealsFound)).append("\n");
    sb.append("    restaurantNotFound: ").append(toIndentedString(restaurantNotFound)).append("\n");
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

