package uk.co.whitbread.basket.generated.models.content;

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
 * DonationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DonationDto {

  @Valid
  private List<String> charityCodes = new ArrayList<>();

  private @Nullable String description;

  private @Nullable String imageSrc;

  private @Nullable String informationBox;

  private @Nullable String name;

  public DonationDto charityCodes(List<String> charityCodes) {
    this.charityCodes = charityCodes;
    return this;
  }

  public DonationDto addCharityCodesItem(String charityCodesItem) {
    if (this.charityCodes == null) {
      this.charityCodes = new ArrayList<>();
    }
    this.charityCodes.add(charityCodesItem);
    return this;
  }

  /**
   * Get charityCodes
   * @return charityCodes
   */
  
  @Schema(name = "charityCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("charityCodes")
  public List<String> getCharityCodes() {
    return charityCodes;
  }

  public void setCharityCodes(List<String> charityCodes) {
    this.charityCodes = charityCodes;
  }

  public DonationDto description(String description) {
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

  public DonationDto imageSrc(String imageSrc) {
    this.imageSrc = imageSrc;
    return this;
  }

  /**
   * Get imageSrc
   * @return imageSrc
   */
  
  @Schema(name = "imageSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("imageSrc")
  public String getImageSrc() {
    return imageSrc;
  }

  public void setImageSrc(String imageSrc) {
    this.imageSrc = imageSrc;
  }

  public DonationDto informationBox(String informationBox) {
    this.informationBox = informationBox;
    return this;
  }

  /**
   * Get informationBox
   * @return informationBox
   */
  
  @Schema(name = "informationBox", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("informationBox")
  public String getInformationBox() {
    return informationBox;
  }

  public void setInformationBox(String informationBox) {
    this.informationBox = informationBox;
  }

  public DonationDto name(String name) {
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
    DonationDto donationDto = (DonationDto) o;
    return Objects.equals(this.charityCodes, donationDto.charityCodes) &&
        Objects.equals(this.description, donationDto.description) &&
        Objects.equals(this.imageSrc, donationDto.imageSrc) &&
        Objects.equals(this.informationBox, donationDto.informationBox) &&
        Objects.equals(this.name, donationDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(charityCodes, description, imageSrc, informationBox, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DonationDto {\n");
    sb.append("    charityCodes: ").append(toIndentedString(charityCodes)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    imageSrc: ").append(toIndentedString(imageSrc)).append("\n");
    sb.append("    informationBox: ").append(toIndentedString(informationBox)).append("\n");
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

