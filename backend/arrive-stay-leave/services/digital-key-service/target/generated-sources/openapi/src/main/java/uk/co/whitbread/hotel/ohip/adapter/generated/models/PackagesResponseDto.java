package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RestaurantDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PackagesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackagesResponseDto {

  private @Nullable Boolean hotelHasCityTaxForBusiness;

  private @Nullable Boolean hotelHasCityTaxForLeisure;

  private @Nullable PackagesDto packages;

  private @Nullable RestaurantDto restaurant;

  public PackagesResponseDto hotelHasCityTaxForBusiness(Boolean hotelHasCityTaxForBusiness) {
    this.hotelHasCityTaxForBusiness = hotelHasCityTaxForBusiness;
    return this;
  }

  /**
   * Get hotelHasCityTaxForBusiness
   * @return hotelHasCityTaxForBusiness
   */
  
  @Schema(name = "hotelHasCityTaxForBusiness", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelHasCityTaxForBusiness")
  public Boolean getHotelHasCityTaxForBusiness() {
    return hotelHasCityTaxForBusiness;
  }

  public void setHotelHasCityTaxForBusiness(Boolean hotelHasCityTaxForBusiness) {
    this.hotelHasCityTaxForBusiness = hotelHasCityTaxForBusiness;
  }

  public PackagesResponseDto hotelHasCityTaxForLeisure(Boolean hotelHasCityTaxForLeisure) {
    this.hotelHasCityTaxForLeisure = hotelHasCityTaxForLeisure;
    return this;
  }

  /**
   * Get hotelHasCityTaxForLeisure
   * @return hotelHasCityTaxForLeisure
   */
  
  @Schema(name = "hotelHasCityTaxForLeisure", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelHasCityTaxForLeisure")
  public Boolean getHotelHasCityTaxForLeisure() {
    return hotelHasCityTaxForLeisure;
  }

  public void setHotelHasCityTaxForLeisure(Boolean hotelHasCityTaxForLeisure) {
    this.hotelHasCityTaxForLeisure = hotelHasCityTaxForLeisure;
  }

  public PackagesResponseDto packages(PackagesDto packages) {
    this.packages = packages;
    return this;
  }

  /**
   * Get packages
   * @return packages
   */
  @Valid 
  @Schema(name = "packages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packages")
  public PackagesDto getPackages() {
    return packages;
  }

  public void setPackages(PackagesDto packages) {
    this.packages = packages;
  }

  public PackagesResponseDto restaurant(RestaurantDto restaurant) {
    this.restaurant = restaurant;
    return this;
  }

  /**
   * Get restaurant
   * @return restaurant
   */
  @Valid 
  @Schema(name = "restaurant", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restaurant")
  public RestaurantDto getRestaurant() {
    return restaurant;
  }

  public void setRestaurant(RestaurantDto restaurant) {
    this.restaurant = restaurant;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackagesResponseDto packagesResponseDto = (PackagesResponseDto) o;
    return Objects.equals(this.hotelHasCityTaxForBusiness, packagesResponseDto.hotelHasCityTaxForBusiness) &&
        Objects.equals(this.hotelHasCityTaxForLeisure, packagesResponseDto.hotelHasCityTaxForLeisure) &&
        Objects.equals(this.packages, packagesResponseDto.packages) &&
        Objects.equals(this.restaurant, packagesResponseDto.restaurant);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelHasCityTaxForBusiness, hotelHasCityTaxForLeisure, packages, restaurant);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackagesResponseDto {\n");
    sb.append("    hotelHasCityTaxForBusiness: ").append(toIndentedString(hotelHasCityTaxForBusiness)).append("\n");
    sb.append("    hotelHasCityTaxForLeisure: ").append(toIndentedString(hotelHasCityTaxForLeisure)).append("\n");
    sb.append("    packages: ").append(toIndentedString(packages)).append("\n");
    sb.append("    restaurant: ").append(toIndentedString(restaurant)).append("\n");
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

