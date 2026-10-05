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
 * CountryInformationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CountryInformationDto {

  private @Nullable String countryCode;

  private @Nullable String countryCodeLegacy;

  private @Nullable String countryName;

  private @Nullable String dialingCode;

  private @Nullable String flagSrc;

  private @Nullable String nationality;

  private @Nullable Boolean passportRequired;

  public CountryInformationDto countryCode(String countryCode) {
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

  public CountryInformationDto countryCodeLegacy(String countryCodeLegacy) {
    this.countryCodeLegacy = countryCodeLegacy;
    return this;
  }

  /**
   * Get countryCodeLegacy
   * @return countryCodeLegacy
   */
  
  @Schema(name = "countryCodeLegacy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryCodeLegacy")
  public String getCountryCodeLegacy() {
    return countryCodeLegacy;
  }

  public void setCountryCodeLegacy(String countryCodeLegacy) {
    this.countryCodeLegacy = countryCodeLegacy;
  }

  public CountryInformationDto countryName(String countryName) {
    this.countryName = countryName;
    return this;
  }

  /**
   * Get countryName
   * @return countryName
   */
  
  @Schema(name = "countryName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryName")
  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public CountryInformationDto dialingCode(String dialingCode) {
    this.dialingCode = dialingCode;
    return this;
  }

  /**
   * Get dialingCode
   * @return dialingCode
   */
  
  @Schema(name = "dialingCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dialingCode")
  public String getDialingCode() {
    return dialingCode;
  }

  public void setDialingCode(String dialingCode) {
    this.dialingCode = dialingCode;
  }

  public CountryInformationDto flagSrc(String flagSrc) {
    this.flagSrc = flagSrc;
    return this;
  }

  /**
   * Get flagSrc
   * @return flagSrc
   */
  
  @Schema(name = "flagSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("flagSrc")
  public String getFlagSrc() {
    return flagSrc;
  }

  public void setFlagSrc(String flagSrc) {
    this.flagSrc = flagSrc;
  }

  public CountryInformationDto nationality(String nationality) {
    this.nationality = nationality;
    return this;
  }

  /**
   * Get nationality
   * @return nationality
   */
  
  @Schema(name = "nationality", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nationality")
  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public CountryInformationDto passportRequired(Boolean passportRequired) {
    this.passportRequired = passportRequired;
    return this;
  }

  /**
   * Get passportRequired
   * @return passportRequired
   */
  
  @Schema(name = "passportRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passportRequired")
  public Boolean getPassportRequired() {
    return passportRequired;
  }

  public void setPassportRequired(Boolean passportRequired) {
    this.passportRequired = passportRequired;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CountryInformationDto countryInformationDto = (CountryInformationDto) o;
    return Objects.equals(this.countryCode, countryInformationDto.countryCode) &&
        Objects.equals(this.countryCodeLegacy, countryInformationDto.countryCodeLegacy) &&
        Objects.equals(this.countryName, countryInformationDto.countryName) &&
        Objects.equals(this.dialingCode, countryInformationDto.dialingCode) &&
        Objects.equals(this.flagSrc, countryInformationDto.flagSrc) &&
        Objects.equals(this.nationality, countryInformationDto.nationality) &&
        Objects.equals(this.passportRequired, countryInformationDto.passportRequired);
  }

  @Override
  public int hashCode() {
    return Objects.hash(countryCode, countryCodeLegacy, countryName, dialingCode, flagSrc, nationality, passportRequired);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CountryInformationDto {\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    countryCodeLegacy: ").append(toIndentedString(countryCodeLegacy)).append("\n");
    sb.append("    countryName: ").append(toIndentedString(countryName)).append("\n");
    sb.append("    dialingCode: ").append(toIndentedString(dialingCode)).append("\n");
    sb.append("    flagSrc: ").append(toIndentedString(flagSrc)).append("\n");
    sb.append("    nationality: ").append(toIndentedString(nationality)).append("\n");
    sb.append("    passportRequired: ").append(toIndentedString(passportRequired)).append("\n");
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

