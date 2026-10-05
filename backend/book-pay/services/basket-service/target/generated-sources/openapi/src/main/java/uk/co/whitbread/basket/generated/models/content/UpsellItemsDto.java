package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.AttachmentsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpsellItemsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpsellItemsDto {

  private @Nullable String additionalInfo;

  @Valid
  private List<@Valid AttachmentsDto> attachments = new ArrayList<>();

  private @Nullable String code;

  private @Nullable String description;

  private @Nullable String freeBreakfastCode;

  private @Nullable Integer freeBreakfastMaxPerMeal;

  private @Nullable Boolean freeBreakfastOption;

  private @Nullable Boolean freeBreakfastTrigger;

  @Valid
  private List<String> images = new ArrayList<>();

  private @Nullable String name;

  private @Nullable Integer order;

  private @Nullable String shortDescription;

  private @Nullable Boolean show;

  public UpsellItemsDto additionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
    return this;
  }

  /**
   * Get additionalInfo
   * @return additionalInfo
   */
  
  @Schema(name = "additionalInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("additionalInfo")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public UpsellItemsDto attachments(List<@Valid AttachmentsDto> attachments) {
    this.attachments = attachments;
    return this;
  }

  public UpsellItemsDto addAttachmentsItem(AttachmentsDto attachmentsItem) {
    if (this.attachments == null) {
      this.attachments = new ArrayList<>();
    }
    this.attachments.add(attachmentsItem);
    return this;
  }

  /**
   * Get attachments
   * @return attachments
   */
  @Valid 
  @Schema(name = "attachments", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("attachments")
  public List<@Valid AttachmentsDto> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<@Valid AttachmentsDto> attachments) {
    this.attachments = attachments;
  }

  public UpsellItemsDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public UpsellItemsDto description(String description) {
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

  public UpsellItemsDto freeBreakfastCode(String freeBreakfastCode) {
    this.freeBreakfastCode = freeBreakfastCode;
    return this;
  }

  /**
   * Get freeBreakfastCode
   * @return freeBreakfastCode
   */
  
  @Schema(name = "freeBreakfastCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("freeBreakfastCode")
  public String getFreeBreakfastCode() {
    return freeBreakfastCode;
  }

  public void setFreeBreakfastCode(String freeBreakfastCode) {
    this.freeBreakfastCode = freeBreakfastCode;
  }

  public UpsellItemsDto freeBreakfastMaxPerMeal(Integer freeBreakfastMaxPerMeal) {
    this.freeBreakfastMaxPerMeal = freeBreakfastMaxPerMeal;
    return this;
  }

  /**
   * Get freeBreakfastMaxPerMeal
   * @return freeBreakfastMaxPerMeal
   */
  
  @Schema(name = "freeBreakfastMaxPerMeal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("freeBreakfastMaxPerMeal")
  public Integer getFreeBreakfastMaxPerMeal() {
    return freeBreakfastMaxPerMeal;
  }

  public void setFreeBreakfastMaxPerMeal(Integer freeBreakfastMaxPerMeal) {
    this.freeBreakfastMaxPerMeal = freeBreakfastMaxPerMeal;
  }

  public UpsellItemsDto freeBreakfastOption(Boolean freeBreakfastOption) {
    this.freeBreakfastOption = freeBreakfastOption;
    return this;
  }

  /**
   * Get freeBreakfastOption
   * @return freeBreakfastOption
   */
  
  @Schema(name = "freeBreakfastOption", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("freeBreakfastOption")
  public Boolean getFreeBreakfastOption() {
    return freeBreakfastOption;
  }

  public void setFreeBreakfastOption(Boolean freeBreakfastOption) {
    this.freeBreakfastOption = freeBreakfastOption;
  }

  public UpsellItemsDto freeBreakfastTrigger(Boolean freeBreakfastTrigger) {
    this.freeBreakfastTrigger = freeBreakfastTrigger;
    return this;
  }

  /**
   * Get freeBreakfastTrigger
   * @return freeBreakfastTrigger
   */
  
  @Schema(name = "freeBreakfastTrigger", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("freeBreakfastTrigger")
  public Boolean getFreeBreakfastTrigger() {
    return freeBreakfastTrigger;
  }

  public void setFreeBreakfastTrigger(Boolean freeBreakfastTrigger) {
    this.freeBreakfastTrigger = freeBreakfastTrigger;
  }

  public UpsellItemsDto images(List<String> images) {
    this.images = images;
    return this;
  }

  public UpsellItemsDto addImagesItem(String imagesItem) {
    if (this.images == null) {
      this.images = new ArrayList<>();
    }
    this.images.add(imagesItem);
    return this;
  }

  /**
   * Get images
   * @return images
   */
  
  @Schema(name = "images", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("images")
  public List<String> getImages() {
    return images;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }

  public UpsellItemsDto name(String name) {
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

  public UpsellItemsDto order(Integer order) {
    this.order = order;
    return this;
  }

  /**
   * Get order
   * @return order
   */
  
  @Schema(name = "order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("order")
  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public UpsellItemsDto shortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
    return this;
  }

  /**
   * Get shortDescription
   * @return shortDescription
   */
  
  @Schema(name = "shortDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shortDescription")
  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public UpsellItemsDto show(Boolean show) {
    this.show = show;
    return this;
  }

  /**
   * Get show
   * @return show
   */
  
  @Schema(name = "show", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("show")
  public Boolean getShow() {
    return show;
  }

  public void setShow(Boolean show) {
    this.show = show;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpsellItemsDto upsellItemsDto = (UpsellItemsDto) o;
    return Objects.equals(this.additionalInfo, upsellItemsDto.additionalInfo) &&
        Objects.equals(this.attachments, upsellItemsDto.attachments) &&
        Objects.equals(this.code, upsellItemsDto.code) &&
        Objects.equals(this.description, upsellItemsDto.description) &&
        Objects.equals(this.freeBreakfastCode, upsellItemsDto.freeBreakfastCode) &&
        Objects.equals(this.freeBreakfastMaxPerMeal, upsellItemsDto.freeBreakfastMaxPerMeal) &&
        Objects.equals(this.freeBreakfastOption, upsellItemsDto.freeBreakfastOption) &&
        Objects.equals(this.freeBreakfastTrigger, upsellItemsDto.freeBreakfastTrigger) &&
        Objects.equals(this.images, upsellItemsDto.images) &&
        Objects.equals(this.name, upsellItemsDto.name) &&
        Objects.equals(this.order, upsellItemsDto.order) &&
        Objects.equals(this.shortDescription, upsellItemsDto.shortDescription) &&
        Objects.equals(this.show, upsellItemsDto.show);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalInfo, attachments, code, description, freeBreakfastCode, freeBreakfastMaxPerMeal, freeBreakfastOption, freeBreakfastTrigger, images, name, order, shortDescription, show);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpsellItemsDto {\n");
    sb.append("    additionalInfo: ").append(toIndentedString(additionalInfo)).append("\n");
    sb.append("    attachments: ").append(toIndentedString(attachments)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    freeBreakfastCode: ").append(toIndentedString(freeBreakfastCode)).append("\n");
    sb.append("    freeBreakfastMaxPerMeal: ").append(toIndentedString(freeBreakfastMaxPerMeal)).append("\n");
    sb.append("    freeBreakfastOption: ").append(toIndentedString(freeBreakfastOption)).append("\n");
    sb.append("    freeBreakfastTrigger: ").append(toIndentedString(freeBreakfastTrigger)).append("\n");
    sb.append("    images: ").append(toIndentedString(images)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
    sb.append("    shortDescription: ").append(toIndentedString(shortDescription)).append("\n");
    sb.append("    show: ").append(toIndentedString(show)).append("\n");
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

