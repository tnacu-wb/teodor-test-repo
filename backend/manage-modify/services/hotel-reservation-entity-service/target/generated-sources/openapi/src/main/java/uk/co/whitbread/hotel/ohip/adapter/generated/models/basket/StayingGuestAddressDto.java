package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

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
 * StayingGuestAddressDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class StayingGuestAddressDto {

  private @Nullable String addressLine1;

  private @Nullable String addressLine2;

  private @Nullable String addressLine3;

  private @Nullable String addressLine4;

  private @Nullable String addressType;

  private @Nullable String cityName;

  private @Nullable String companyName;

  private @Nullable String countryCode;

  private @Nullable String postalCode;

  private @Nullable Boolean valid;

  public StayingGuestAddressDto addressLine1(String addressLine1) {
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

  public StayingGuestAddressDto addressLine2(String addressLine2) {
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

  public StayingGuestAddressDto addressLine3(String addressLine3) {
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

  public StayingGuestAddressDto addressLine4(String addressLine4) {
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

  public StayingGuestAddressDto addressType(String addressType) {
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

  public StayingGuestAddressDto cityName(String cityName) {
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

  public StayingGuestAddressDto companyName(String companyName) {
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

  public StayingGuestAddressDto countryCode(String countryCode) {
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

  public StayingGuestAddressDto postalCode(String postalCode) {
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

  public StayingGuestAddressDto valid(Boolean valid) {
    this.valid = valid;
    return this;
  }

  /**
   * Get valid
   * @return valid
   */
  
  @Schema(name = "valid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("valid")
  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StayingGuestAddressDto stayingGuestAddressDto = (StayingGuestAddressDto) o;
    return Objects.equals(this.addressLine1, stayingGuestAddressDto.addressLine1) &&
        Objects.equals(this.addressLine2, stayingGuestAddressDto.addressLine2) &&
        Objects.equals(this.addressLine3, stayingGuestAddressDto.addressLine3) &&
        Objects.equals(this.addressLine4, stayingGuestAddressDto.addressLine4) &&
        Objects.equals(this.addressType, stayingGuestAddressDto.addressType) &&
        Objects.equals(this.cityName, stayingGuestAddressDto.cityName) &&
        Objects.equals(this.companyName, stayingGuestAddressDto.companyName) &&
        Objects.equals(this.countryCode, stayingGuestAddressDto.countryCode) &&
        Objects.equals(this.postalCode, stayingGuestAddressDto.postalCode) &&
        Objects.equals(this.valid, stayingGuestAddressDto.valid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressLine1, addressLine2, addressLine3, addressLine4, addressType, cityName, companyName, countryCode, postalCode, valid);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StayingGuestAddressDto {\n");
    sb.append("    addressLine1: ").append(toIndentedString(addressLine1)).append("\n");
    sb.append("    addressLine2: ").append(toIndentedString(addressLine2)).append("\n");
    sb.append("    addressLine3: ").append(toIndentedString(addressLine3)).append("\n");
    sb.append("    addressLine4: ").append(toIndentedString(addressLine4)).append("\n");
    sb.append("    addressType: ").append(toIndentedString(addressType)).append("\n");
    sb.append("    cityName: ").append(toIndentedString(cityName)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
    sb.append("    valid: ").append(toIndentedString(valid)).append("\n");
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

