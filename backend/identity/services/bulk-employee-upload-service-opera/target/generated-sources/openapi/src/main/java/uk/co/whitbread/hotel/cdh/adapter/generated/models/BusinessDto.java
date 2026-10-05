package uk.co.whitbread.hotel.cdh.adapter.generated.models;

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
 * BusinessDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:35.320601+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessDto {

  private @Nullable Boolean dismissMPILink;

  private @Nullable Boolean miSetupRequired;

  private @Nullable String myPILink;

  private @Nullable Boolean tethered;

  public BusinessDto dismissMPILink(Boolean dismissMPILink) {
    this.dismissMPILink = dismissMPILink;
    return this;
  }

  /**
   * Get dismissMPILink
   * @return dismissMPILink
   */
  
  @Schema(name = "dismissMPILink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dismissMPILink")
  public Boolean getDismissMPILink() {
    return dismissMPILink;
  }

  public void setDismissMPILink(Boolean dismissMPILink) {
    this.dismissMPILink = dismissMPILink;
  }

  public BusinessDto miSetupRequired(Boolean miSetupRequired) {
    this.miSetupRequired = miSetupRequired;
    return this;
  }

  /**
   * Get miSetupRequired
   * @return miSetupRequired
   */
  
  @Schema(name = "miSetupRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("miSetupRequired")
  public Boolean getMiSetupRequired() {
    return miSetupRequired;
  }

  public void setMiSetupRequired(Boolean miSetupRequired) {
    this.miSetupRequired = miSetupRequired;
  }

  public BusinessDto myPILink(String myPILink) {
    this.myPILink = myPILink;
    return this;
  }

  /**
   * Get myPILink
   * @return myPILink
   */
  
  @Schema(name = "myPILink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("myPILink")
  public String getMyPILink() {
    return myPILink;
  }

  public void setMyPILink(String myPILink) {
    this.myPILink = myPILink;
  }

  public BusinessDto tethered(Boolean tethered) {
    this.tethered = tethered;
    return this;
  }

  /**
   * Get tethered
   * @return tethered
   */
  
  @Schema(name = "tethered", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tethered")
  public Boolean getTethered() {
    return tethered;
  }

  public void setTethered(Boolean tethered) {
    this.tethered = tethered;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessDto businessDto = (BusinessDto) o;
    return Objects.equals(this.dismissMPILink, businessDto.dismissMPILink) &&
        Objects.equals(this.miSetupRequired, businessDto.miSetupRequired) &&
        Objects.equals(this.myPILink, businessDto.myPILink) &&
        Objects.equals(this.tethered, businessDto.tethered);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dismissMPILink, miSetupRequired, myPILink, tethered);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessDto {\n");
    sb.append("    dismissMPILink: ").append(toIndentedString(dismissMPILink)).append("\n");
    sb.append("    miSetupRequired: ").append(toIndentedString(miSetupRequired)).append("\n");
    sb.append("    myPILink: ").append(toIndentedString(myPILink)).append("\n");
    sb.append("    tethered: ").append(toIndentedString(tethered)).append("\n");
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

