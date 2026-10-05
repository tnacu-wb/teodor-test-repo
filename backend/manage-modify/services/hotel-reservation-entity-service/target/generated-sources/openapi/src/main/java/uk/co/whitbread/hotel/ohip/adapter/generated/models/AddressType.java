package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Country;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AddressType
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AddressType {

  @Valid
  private List<String> addressLine = new ArrayList<>();

  private @Nullable String cityName;

  private @Nullable Country country;

  private @Nullable Boolean isValidated;

  private @Nullable String language;

  private @Nullable String postalCode;

  private @Nullable Boolean primaryInd;

  private @Nullable String state;

  private @Nullable String type;

  public AddressType addressLine(List<String> addressLine) {
    this.addressLine = addressLine;
    return this;
  }

  public AddressType addAddressLineItem(String addressLineItem) {
    if (this.addressLine == null) {
      this.addressLine = new ArrayList<>();
    }
    this.addressLine.add(addressLineItem);
    return this;
  }

  /**
   * Get addressLine
   * @return addressLine
   */
  
  @Schema(name = "addressLine", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine")
  public List<String> getAddressLine() {
    return addressLine;
  }

  public void setAddressLine(List<String> addressLine) {
    this.addressLine = addressLine;
  }

  public AddressType cityName(String cityName) {
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

  public AddressType country(Country country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   * @return country
   */
  @Valid 
  @Schema(name = "country", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("country")
  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public AddressType isValidated(Boolean isValidated) {
    this.isValidated = isValidated;
    return this;
  }

  /**
   * Get isValidated
   * @return isValidated
   */
  
  @Schema(name = "isValidated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isValidated")
  public Boolean getIsValidated() {
    return isValidated;
  }

  public void setIsValidated(Boolean isValidated) {
    this.isValidated = isValidated;
  }

  public AddressType language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public AddressType postalCode(String postalCode) {
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

  public AddressType primaryInd(Boolean primaryInd) {
    this.primaryInd = primaryInd;
    return this;
  }

  /**
   * Get primaryInd
   * @return primaryInd
   */
  
  @Schema(name = "primaryInd", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("primaryInd")
  public Boolean getPrimaryInd() {
    return primaryInd;
  }

  public void setPrimaryInd(Boolean primaryInd) {
    this.primaryInd = primaryInd;
  }

  public AddressType state(String state) {
    this.state = state;
    return this;
  }

  /**
   * Get state
   * @return state
   */
  
  @Schema(name = "state", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("state")
  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public AddressType type(String type) {
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
    AddressType addressType = (AddressType) o;
    return Objects.equals(this.addressLine, addressType.addressLine) &&
        Objects.equals(this.cityName, addressType.cityName) &&
        Objects.equals(this.country, addressType.country) &&
        Objects.equals(this.isValidated, addressType.isValidated) &&
        Objects.equals(this.language, addressType.language) &&
        Objects.equals(this.postalCode, addressType.postalCode) &&
        Objects.equals(this.primaryInd, addressType.primaryInd) &&
        Objects.equals(this.state, addressType.state) &&
        Objects.equals(this.type, addressType.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressLine, cityName, country, isValidated, language, postalCode, primaryInd, state, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddressType {\n");
    sb.append("    addressLine: ").append(toIndentedString(addressLine)).append("\n");
    sb.append("    cityName: ").append(toIndentedString(cityName)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    isValidated: ").append(toIndentedString(isValidated)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
    sb.append("    primaryInd: ").append(toIndentedString(primaryInd)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

