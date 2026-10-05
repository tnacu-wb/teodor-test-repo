package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.InfoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PrivacyPolicyDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PrivacyPolicyDto {

  private @Nullable String description;

  private @Nullable String linkLabel;

  private @Nullable String linkSrc;

  @Valid
  private List<@Valid InfoDto> moreInfo = new ArrayList<>();

  private @Nullable String moreInfoLabel;

  private @Nullable String name;

  public PrivacyPolicyDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public PrivacyPolicyDto linkLabel(String linkLabel) {
    this.linkLabel = linkLabel;
    return this;
  }

  /**
   * Get linkLabel
   * @return linkLabel
   */
  
  @Schema(name = "linkLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkLabel")
  public String getLinkLabel() {
    return linkLabel;
  }

  public void setLinkLabel(String linkLabel) {
    this.linkLabel = linkLabel;
  }

  public PrivacyPolicyDto linkSrc(String linkSrc) {
    this.linkSrc = linkSrc;
    return this;
  }

  /**
   * Get linkSrc
   * @return linkSrc
   */
  
  @Schema(name = "linkSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkSrc")
  public String getLinkSrc() {
    return linkSrc;
  }

  public void setLinkSrc(String linkSrc) {
    this.linkSrc = linkSrc;
  }

  public PrivacyPolicyDto moreInfo(List<@Valid InfoDto> moreInfo) {
    this.moreInfo = moreInfo;
    return this;
  }

  public PrivacyPolicyDto addMoreInfoItem(InfoDto moreInfoItem) {
    if (this.moreInfo == null) {
      this.moreInfo = new ArrayList<>();
    }
    this.moreInfo.add(moreInfoItem);
    return this;
  }

  /**
   * Get moreInfo
   * @return moreInfo
   */
  @Valid 
  @Schema(name = "moreInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("moreInfo")
  public List<@Valid InfoDto> getMoreInfo() {
    return moreInfo;
  }

  public void setMoreInfo(List<@Valid InfoDto> moreInfo) {
    this.moreInfo = moreInfo;
  }

  public PrivacyPolicyDto moreInfoLabel(String moreInfoLabel) {
    this.moreInfoLabel = moreInfoLabel;
    return this;
  }

  /**
   * Get moreInfoLabel
   * @return moreInfoLabel
   */
  
  @Schema(name = "moreInfoLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("moreInfoLabel")
  public String getMoreInfoLabel() {
    return moreInfoLabel;
  }

  public void setMoreInfoLabel(String moreInfoLabel) {
    this.moreInfoLabel = moreInfoLabel;
  }

  public PrivacyPolicyDto name(String name) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PrivacyPolicyDto privacyPolicyDto = (PrivacyPolicyDto) o;
    return Objects.equals(this.description, privacyPolicyDto.description) &&
        Objects.equals(this.linkLabel, privacyPolicyDto.linkLabel) &&
        Objects.equals(this.linkSrc, privacyPolicyDto.linkSrc) &&
        Objects.equals(this.moreInfo, privacyPolicyDto.moreInfo) &&
        Objects.equals(this.moreInfoLabel, privacyPolicyDto.moreInfoLabel) &&
        Objects.equals(this.name, privacyPolicyDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, linkLabel, linkSrc, moreInfo, moreInfoLabel, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PrivacyPolicyDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    linkLabel: ").append(toIndentedString(linkLabel)).append("\n");
    sb.append("    linkSrc: ").append(toIndentedString(linkSrc)).append("\n");
    sb.append("    moreInfo: ").append(toIndentedString(moreInfo)).append("\n");
    sb.append("    moreInfoLabel: ").append(toIndentedString(moreInfoLabel)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

