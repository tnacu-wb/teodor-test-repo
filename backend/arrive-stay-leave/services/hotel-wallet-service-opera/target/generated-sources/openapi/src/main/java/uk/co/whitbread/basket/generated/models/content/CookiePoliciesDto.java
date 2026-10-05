package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.IntroViewDto;
import uk.co.whitbread.basket.generated.models.content.ManageViewDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CookiePoliciesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CookiePoliciesDto {

  private @Nullable String brand;

  private @Nullable IntroViewDto introView;

  private @Nullable ManageViewDto manageView;

  private @Nullable String version;

  public CookiePoliciesDto brand(String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand
   */
  
  @Schema(name = "brand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public CookiePoliciesDto introView(IntroViewDto introView) {
    this.introView = introView;
    return this;
  }

  /**
   * Get introView
   * @return introView
   */
  @Valid 
  @Schema(name = "introView", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("introView")
  public IntroViewDto getIntroView() {
    return introView;
  }

  public void setIntroView(IntroViewDto introView) {
    this.introView = introView;
  }

  public CookiePoliciesDto manageView(ManageViewDto manageView) {
    this.manageView = manageView;
    return this;
  }

  /**
   * Get manageView
   * @return manageView
   */
  @Valid 
  @Schema(name = "manageView", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("manageView")
  public ManageViewDto getManageView() {
    return manageView;
  }

  public void setManageView(ManageViewDto manageView) {
    this.manageView = manageView;
  }

  public CookiePoliciesDto version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Get version
   * @return version
   */
  
  @Schema(name = "version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CookiePoliciesDto cookiePoliciesDto = (CookiePoliciesDto) o;
    return Objects.equals(this.brand, cookiePoliciesDto.brand) &&
        Objects.equals(this.introView, cookiePoliciesDto.introView) &&
        Objects.equals(this.manageView, cookiePoliciesDto.manageView) &&
        Objects.equals(this.version, cookiePoliciesDto.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brand, introView, manageView, version);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CookiePoliciesDto {\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    introView: ").append(toIndentedString(introView)).append("\n");
    sb.append("    manageView: ").append(toIndentedString(manageView)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
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

