package uk.co.whitbread.basket.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Card holder address
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Schema(name = "Address", description = "Card holder address")
@JsonTypeName("Address")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:02.841275+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AddressDto {

  private String countryCode = "GB";

  private @Nullable String line1;

  private @Nullable String line2;

  private @Nullable String line3;

  private @Nullable String line4;

  private @Nullable String postalCode;

  public AddressDto countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * Country Code
   * @return countryCode
   */
  
  @Schema(name = "countryCode", example = "GB", description = "Country Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryCode")
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public AddressDto line1(String line1) {
    this.line1 = line1;
    return this;
  }

  /**
   * Address line 1
   * @return line1
   */
  
  @Schema(name = "line1", example = "Whitbread Group PLC", description = "Address line 1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("line1")
  public String getLine1() {
    return line1;
  }

  public void setLine1(String line1) {
    this.line1 = line1;
  }

  public AddressDto line2(String line2) {
    this.line2 = line2;
    return this;
  }

  /**
   * Address line 2
   * @return line2
   */
  
  @Schema(name = "line2", example = "Houghton Hall Business Park", description = "Address line 2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("line2")
  public String getLine2() {
    return line2;
  }

  public void setLine2(String line2) {
    this.line2 = line2;
  }

  public AddressDto line3(String line3) {
    this.line3 = line3;
    return this;
  }

  /**
   * Address line 3
   * @return line3
   */
  
  @Schema(name = "line3", example = "Porz Avenue", description = "Address line 3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("line3")
  public String getLine3() {
    return line3;
  }

  public void setLine3(String line3) {
    this.line3 = line3;
  }

  public AddressDto line4(String line4) {
    this.line4 = line4;
    return this;
  }

  /**
   * Address line 4
   * @return line4
   */
  
  @Schema(name = "line4", example = "Dunstable", description = "Address line 4", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("line4")
  public String getLine4() {
    return line4;
  }

  public void setLine4(String line4) {
    this.line4 = line4;
  }

  public AddressDto postalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Postal code.
   * @return postalCode
   */
  
  @Schema(name = "postalCode", example = "LU5 5XE", description = "Postal code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    AddressDto address = (AddressDto) o;
    return Objects.equals(this.countryCode, address.countryCode) &&
        Objects.equals(this.line1, address.line1) &&
        Objects.equals(this.line2, address.line2) &&
        Objects.equals(this.line3, address.line3) &&
        Objects.equals(this.line4, address.line4) &&
        Objects.equals(this.postalCode, address.postalCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(countryCode, line1, line2, line3, line4, postalCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddressDto {\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    line1: ").append(toIndentedString(line1)).append("\n");
    sb.append("    line2: ").append(toIndentedString(line2)).append("\n");
    sb.append("    line3: ").append(toIndentedString(line3)).append("\n");
    sb.append("    line4: ").append(toIndentedString(line4)).append("\n");
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

