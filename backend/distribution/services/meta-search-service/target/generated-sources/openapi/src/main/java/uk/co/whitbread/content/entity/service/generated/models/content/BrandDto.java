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
 * BrandDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BrandDto {

  private @Nullable String hub;

  private @Nullable String hubBadge;

  private @Nullable String hubLogo;

  private @Nullable String pi;

  private @Nullable String piLogo;

  private @Nullable String pid;

  private @Nullable String pidLogo;

  private @Nullable String zip;

  private @Nullable String zipBadge;

  private @Nullable String zipLogo;

  private @Nullable String zipLogoWhite;

  public BrandDto hub(String hub) {
    this.hub = hub;
    return this;
  }

  /**
   * Get hub
   * @return hub
   */
  
  @Schema(name = "hub", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hub")
  public String getHub() {
    return hub;
  }

  public void setHub(String hub) {
    this.hub = hub;
  }

  public BrandDto hubBadge(String hubBadge) {
    this.hubBadge = hubBadge;
    return this;
  }

  /**
   * Get hubBadge
   * @return hubBadge
   */
  
  @Schema(name = "hubBadge", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hubBadge")
  public String getHubBadge() {
    return hubBadge;
  }

  public void setHubBadge(String hubBadge) {
    this.hubBadge = hubBadge;
  }

  public BrandDto hubLogo(String hubLogo) {
    this.hubLogo = hubLogo;
    return this;
  }

  /**
   * Get hubLogo
   * @return hubLogo
   */
  
  @Schema(name = "hubLogo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hubLogo")
  public String getHubLogo() {
    return hubLogo;
  }

  public void setHubLogo(String hubLogo) {
    this.hubLogo = hubLogo;
  }

  public BrandDto pi(String pi) {
    this.pi = pi;
    return this;
  }

  /**
   * Get pi
   * @return pi
   */
  
  @Schema(name = "pi", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pi")
  public String getPi() {
    return pi;
  }

  public void setPi(String pi) {
    this.pi = pi;
  }

  public BrandDto piLogo(String piLogo) {
    this.piLogo = piLogo;
    return this;
  }

  /**
   * Get piLogo
   * @return piLogo
   */
  
  @Schema(name = "piLogo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("piLogo")
  public String getPiLogo() {
    return piLogo;
  }

  public void setPiLogo(String piLogo) {
    this.piLogo = piLogo;
  }

  public BrandDto pid(String pid) {
    this.pid = pid;
    return this;
  }

  /**
   * Get pid
   * @return pid
   */
  
  @Schema(name = "pid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pid")
  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public BrandDto pidLogo(String pidLogo) {
    this.pidLogo = pidLogo;
    return this;
  }

  /**
   * Get pidLogo
   * @return pidLogo
   */
  
  @Schema(name = "pidLogo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pidLogo")
  public String getPidLogo() {
    return pidLogo;
  }

  public void setPidLogo(String pidLogo) {
    this.pidLogo = pidLogo;
  }

  public BrandDto zip(String zip) {
    this.zip = zip;
    return this;
  }

  /**
   * Get zip
   * @return zip
   */
  
  @Schema(name = "zip", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("zip")
  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public BrandDto zipBadge(String zipBadge) {
    this.zipBadge = zipBadge;
    return this;
  }

  /**
   * Get zipBadge
   * @return zipBadge
   */
  
  @Schema(name = "zipBadge", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("zipBadge")
  public String getZipBadge() {
    return zipBadge;
  }

  public void setZipBadge(String zipBadge) {
    this.zipBadge = zipBadge;
  }

  public BrandDto zipLogo(String zipLogo) {
    this.zipLogo = zipLogo;
    return this;
  }

  /**
   * Get zipLogo
   * @return zipLogo
   */
  
  @Schema(name = "zipLogo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("zipLogo")
  public String getZipLogo() {
    return zipLogo;
  }

  public void setZipLogo(String zipLogo) {
    this.zipLogo = zipLogo;
  }

  public BrandDto zipLogoWhite(String zipLogoWhite) {
    this.zipLogoWhite = zipLogoWhite;
    return this;
  }

  /**
   * Get zipLogoWhite
   * @return zipLogoWhite
   */
  
  @Schema(name = "zipLogoWhite", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("zipLogoWhite")
  public String getZipLogoWhite() {
    return zipLogoWhite;
  }

  public void setZipLogoWhite(String zipLogoWhite) {
    this.zipLogoWhite = zipLogoWhite;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BrandDto brandDto = (BrandDto) o;
    return Objects.equals(this.hub, brandDto.hub) &&
        Objects.equals(this.hubBadge, brandDto.hubBadge) &&
        Objects.equals(this.hubLogo, brandDto.hubLogo) &&
        Objects.equals(this.pi, brandDto.pi) &&
        Objects.equals(this.piLogo, brandDto.piLogo) &&
        Objects.equals(this.pid, brandDto.pid) &&
        Objects.equals(this.pidLogo, brandDto.pidLogo) &&
        Objects.equals(this.zip, brandDto.zip) &&
        Objects.equals(this.zipBadge, brandDto.zipBadge) &&
        Objects.equals(this.zipLogo, brandDto.zipLogo) &&
        Objects.equals(this.zipLogoWhite, brandDto.zipLogoWhite);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hub, hubBadge, hubLogo, pi, piLogo, pid, pidLogo, zip, zipBadge, zipLogo, zipLogoWhite);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BrandDto {\n");
    sb.append("    hub: ").append(toIndentedString(hub)).append("\n");
    sb.append("    hubBadge: ").append(toIndentedString(hubBadge)).append("\n");
    sb.append("    hubLogo: ").append(toIndentedString(hubLogo)).append("\n");
    sb.append("    pi: ").append(toIndentedString(pi)).append("\n");
    sb.append("    piLogo: ").append(toIndentedString(piLogo)).append("\n");
    sb.append("    pid: ").append(toIndentedString(pid)).append("\n");
    sb.append("    pidLogo: ").append(toIndentedString(pidLogo)).append("\n");
    sb.append("    zip: ").append(toIndentedString(zip)).append("\n");
    sb.append("    zipBadge: ").append(toIndentedString(zipBadge)).append("\n");
    sb.append("    zipLogo: ").append(toIndentedString(zipLogo)).append("\n");
    sb.append("    zipLogoWhite: ").append(toIndentedString(zipLogoWhite)).append("\n");
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

