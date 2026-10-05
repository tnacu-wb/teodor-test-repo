package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Address Details such as city, state, country, postal code etc.
 */

@Schema(name = "Address", description = "Address Details such as city, state, country, postal code etc.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Address {

  @Valid
  private List<String> addressLine = new ArrayList<>();

  private @Nullable String countryCode;

  private @Nullable String cityName;

  private @Nullable String stateProv;

  private @Nullable String postalCode;

  public Address addressLine(List<String> addressLine) {
    this.addressLine = addressLine;
    return this;
  }

  public Address addAddressLineItem(String addressLineItem) {
    if (this.addressLine == null) {
      this.addressLine = new ArrayList<>();
    }
    this.addressLine.add(addressLineItem);
    return this;
  }

  /**
   * The property's street address.
   * @return addressLine
   */
  @Size(min = 1, max = 2) 
  @Schema(name = "addressLine", example = "[Street 123, Box 1]", description = "The property's street address.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine")
  public List<String> getAddressLine() {
    return addressLine;
  }

  public void setAddressLine(List<String> addressLine) {
    this.addressLine = addressLine;
  }

  public Address countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * The property's two letter ISO country code.
   * @return countryCode
   */
  
  @Schema(name = "countryCode", example = "US", description = "The property's two letter ISO country code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryCode")
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public Address cityName(String cityName) {
    this.cityName = cityName;
    return this;
  }

  /**
   * The city where the property is located.
   * @return cityName
   */
  
  @Schema(name = "cityName", example = "Miami", description = "The city where the property is located.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cityName")
  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public Address stateProv(String stateProv) {
    this.stateProv = stateProv;
    return this;
  }

  /**
   * The state where the property is located.
   * @return stateProv
   */
  
  @Schema(name = "stateProv", example = "Florida", description = "The state where the property is located.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stateProv")
  public String getStateProv() {
    return stateProv;
  }

  public void setStateProv(String stateProv) {
    this.stateProv = stateProv;
  }

  public Address postalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * The property's postal (ZIP) code.
   * @return postalCode
   */
  
  @Schema(name = "postalCode", example = "90210", description = "The property's postal (ZIP) code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    Address address = (Address) o;
    return Objects.equals(this.addressLine, address.addressLine) &&
        Objects.equals(this.countryCode, address.countryCode) &&
        Objects.equals(this.cityName, address.cityName) &&
        Objects.equals(this.stateProv, address.stateProv) &&
        Objects.equals(this.postalCode, address.postalCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressLine, countryCode, cityName, stateProv, postalCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Address {\n");
    sb.append("    addressLine: ").append(toIndentedString(addressLine)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    cityName: ").append(toIndentedString(cityName)).append("\n");
    sb.append("    stateProv: ").append(toIndentedString(stateProv)).append("\n");
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

