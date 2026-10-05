package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AddressDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CompanyProfileDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyProfileDto {

  private @Nullable Boolean active;

  private @Nullable AddressDto address;

  private @Nullable String arNumber;

  private @Nullable String companyId;

  private @Nullable String corpId;

  private @Nullable String language;

  private @Nullable String name;

  private @Nullable String profileType;

  private @Nullable Boolean restricted;

  private @Nullable String restrictedReason;

  private @Nullable String telephoneNumber;

  public CompanyProfileDto active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Get active
   * @return active
   */
  
  @Schema(name = "active", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public CompanyProfileDto address(AddressDto address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @Valid 
  @Schema(name = "address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("address")
  public AddressDto getAddress() {
    return address;
  }

  public void setAddress(AddressDto address) {
    this.address = address;
  }

  public CompanyProfileDto arNumber(String arNumber) {
    this.arNumber = arNumber;
    return this;
  }

  /**
   * Get arNumber
   * @return arNumber
   */
  
  @Schema(name = "arNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arNumber")
  public String getArNumber() {
    return arNumber;
  }

  public void setArNumber(String arNumber) {
    this.arNumber = arNumber;
  }

  public CompanyProfileDto companyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  /**
   * Get companyId
   * @return companyId
   */
  
  @Schema(name = "companyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyId")
  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public CompanyProfileDto corpId(String corpId) {
    this.corpId = corpId;
    return this;
  }

  /**
   * Get corpId
   * @return corpId
   */
  
  @Schema(name = "corpId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("corpId")
  public String getCorpId() {
    return corpId;
  }

  public void setCorpId(String corpId) {
    this.corpId = corpId;
  }

  public CompanyProfileDto language(String language) {
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

  public CompanyProfileDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CompanyProfileDto profileType(String profileType) {
    this.profileType = profileType;
    return this;
  }

  /**
   * Get profileType
   * @return profileType
   */
  
  @Schema(name = "profileType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profileType")
  public String getProfileType() {
    return profileType;
  }

  public void setProfileType(String profileType) {
    this.profileType = profileType;
  }

  public CompanyProfileDto restricted(Boolean restricted) {
    this.restricted = restricted;
    return this;
  }

  /**
   * Get restricted
   * @return restricted
   */
  
  @Schema(name = "restricted", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restricted")
  public Boolean getRestricted() {
    return restricted;
  }

  public void setRestricted(Boolean restricted) {
    this.restricted = restricted;
  }

  public CompanyProfileDto restrictedReason(String restrictedReason) {
    this.restrictedReason = restrictedReason;
    return this;
  }

  /**
   * Get restrictedReason
   * @return restrictedReason
   */
  
  @Schema(name = "restrictedReason", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictedReason")
  public String getRestrictedReason() {
    return restrictedReason;
  }

  public void setRestrictedReason(String restrictedReason) {
    this.restrictedReason = restrictedReason;
  }

  public CompanyProfileDto telephoneNumber(String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
    return this;
  }

  /**
   * Get telephoneNumber
   * @return telephoneNumber
   */
  
  @Schema(name = "telephoneNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("telephoneNumber")
  public String getTelephoneNumber() {
    return telephoneNumber;
  }

  public void setTelephoneNumber(String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyProfileDto companyProfileDto = (CompanyProfileDto) o;
    return Objects.equals(this.active, companyProfileDto.active) &&
        Objects.equals(this.address, companyProfileDto.address) &&
        Objects.equals(this.arNumber, companyProfileDto.arNumber) &&
        Objects.equals(this.companyId, companyProfileDto.companyId) &&
        Objects.equals(this.corpId, companyProfileDto.corpId) &&
        Objects.equals(this.language, companyProfileDto.language) &&
        Objects.equals(this.name, companyProfileDto.name) &&
        Objects.equals(this.profileType, companyProfileDto.profileType) &&
        Objects.equals(this.restricted, companyProfileDto.restricted) &&
        Objects.equals(this.restrictedReason, companyProfileDto.restrictedReason) &&
        Objects.equals(this.telephoneNumber, companyProfileDto.telephoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(active, address, arNumber, companyId, corpId, language, name, profileType, restricted, restrictedReason, telephoneNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyProfileDto {\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    arNumber: ").append(toIndentedString(arNumber)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    corpId: ").append(toIndentedString(corpId)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    profileType: ").append(toIndentedString(profileType)).append("\n");
    sb.append("    restricted: ").append(toIndentedString(restricted)).append("\n");
    sb.append("    restrictedReason: ").append(toIndentedString(restrictedReason)).append("\n");
    sb.append("    telephoneNumber: ").append(toIndentedString(telephoneNumber)).append("\n");
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

