package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * ReservationByIdGuestAddressDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByIdGuestAddressDto {

  private @Nullable String addressId;

  private @Nullable String addressLine1;

  private @Nullable String addressLine2;

  private @Nullable String addressLine3;

  private @Nullable String addressLine4;

  private @Nullable String addressType;

  private @Nullable String cityName;

  private @Nullable String companyName;

  private @Nullable String countryCode;

  private @Nullable String postalCode;

  public ReservationByIdGuestAddressDto addressId(String addressId) {
    this.addressId = addressId;
    return this;
  }

  /**
   * Get addressId
   * @return addressId
   */
  
  @Schema(name = "addressId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressId")
  public String getAddressId() {
    return addressId;
  }

  public void setAddressId(String addressId) {
    this.addressId = addressId;
  }

  public ReservationByIdGuestAddressDto addressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
    return this;
  }

  /**
   * Get addressLine1
   * @return addressLine1
   */
  
  @Schema(name = "addressLine1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine1")
  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public ReservationByIdGuestAddressDto addressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
    return this;
  }

  /**
   * Get addressLine2
   * @return addressLine2
   */
  
  @Schema(name = "addressLine2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine2")
  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public ReservationByIdGuestAddressDto addressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
    return this;
  }

  /**
   * Get addressLine3
   * @return addressLine3
   */
  
  @Schema(name = "addressLine3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine3")
  public String getAddressLine3() {
    return addressLine3;
  }

  public void setAddressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
  }

  public ReservationByIdGuestAddressDto addressLine4(String addressLine4) {
    this.addressLine4 = addressLine4;
    return this;
  }

  /**
   * Get addressLine4
   * @return addressLine4
   */
  
  @Schema(name = "addressLine4", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine4")
  public String getAddressLine4() {
    return addressLine4;
  }

  public void setAddressLine4(String addressLine4) {
    this.addressLine4 = addressLine4;
  }

  public ReservationByIdGuestAddressDto addressType(String addressType) {
    this.addressType = addressType;
    return this;
  }

  /**
   * Get addressType
   * @return addressType
   */
  
  @Schema(name = "addressType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressType")
  public String getAddressType() {
    return addressType;
  }

  public void setAddressType(String addressType) {
    this.addressType = addressType;
  }

  public ReservationByIdGuestAddressDto cityName(String cityName) {
    this.cityName = cityName;
    return this;
  }

  /**
   * Get cityName
   * @return cityName
   */
  
  @Schema(name = "cityName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cityName")
  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public ReservationByIdGuestAddressDto companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * Get companyName
   * @return companyName
   */
  
  @Schema(name = "companyName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyName")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public ReservationByIdGuestAddressDto countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * Get countryCode
   * @return countryCode
   */
  
  @Schema(name = "countryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryCode")
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public ReservationByIdGuestAddressDto postalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Get postalCode
   * @return postalCode
   */
  
  @Schema(name = "postalCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postalCode")
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationByIdGuestAddressDto reservationByIdGuestAddressDto = (ReservationByIdGuestAddressDto) o;
    return Objects.equals(this.addressId, reservationByIdGuestAddressDto.addressId) &&
        Objects.equals(this.addressLine1, reservationByIdGuestAddressDto.addressLine1) &&
        Objects.equals(this.addressLine2, reservationByIdGuestAddressDto.addressLine2) &&
        Objects.equals(this.addressLine3, reservationByIdGuestAddressDto.addressLine3) &&
        Objects.equals(this.addressLine4, reservationByIdGuestAddressDto.addressLine4) &&
        Objects.equals(this.addressType, reservationByIdGuestAddressDto.addressType) &&
        Objects.equals(this.cityName, reservationByIdGuestAddressDto.cityName) &&
        Objects.equals(this.companyName, reservationByIdGuestAddressDto.companyName) &&
        Objects.equals(this.countryCode, reservationByIdGuestAddressDto.countryCode) &&
        Objects.equals(this.postalCode, reservationByIdGuestAddressDto.postalCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressId, addressLine1, addressLine2, addressLine3, addressLine4, addressType, cityName, companyName, countryCode, postalCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByIdGuestAddressDto {\n");
    sb.append("    addressId: ").append(toIndentedString(addressId)).append("\n");
    sb.append("    addressLine1: ").append(toIndentedString(addressLine1)).append("\n");
    sb.append("    addressLine2: ").append(toIndentedString(addressLine2)).append("\n");
    sb.append("    addressLine3: ").append(toIndentedString(addressLine3)).append("\n");
    sb.append("    addressLine4: ").append(toIndentedString(addressLine4)).append("\n");
    sb.append("    addressType: ").append(toIndentedString(addressType)).append("\n");
    sb.append("    cityName: ").append(toIndentedString(cityName)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
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

