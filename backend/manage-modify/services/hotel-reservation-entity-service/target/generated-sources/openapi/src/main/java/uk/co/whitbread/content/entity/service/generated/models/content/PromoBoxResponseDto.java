package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PromoBoxResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PromoBoxResponseDto {

  private @Nullable String button;

  private @Nullable String title;

  private @Nullable String whenEmpty;

  private @Nullable String whenInvalid;

  private @Nullable String whenMultipleRedeem;

  private @Nullable String whenSuccess;

  public PromoBoxResponseDto button(String button) {
    this.button = button;
    return this;
  }

  /**
   * Get button
   * @return button
   */
  
  @Schema(name = "button", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("button")
  public String getButton() {
    return button;
  }

  public void setButton(String button) {
    this.button = button;
  }

  public PromoBoxResponseDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public PromoBoxResponseDto whenEmpty(String whenEmpty) {
    this.whenEmpty = whenEmpty;
    return this;
  }

  /**
   * Get whenEmpty
   * @return whenEmpty
   */
  
  @Schema(name = "whenEmpty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("whenEmpty")
  public String getWhenEmpty() {
    return whenEmpty;
  }

  public void setWhenEmpty(String whenEmpty) {
    this.whenEmpty = whenEmpty;
  }

  public PromoBoxResponseDto whenInvalid(String whenInvalid) {
    this.whenInvalid = whenInvalid;
    return this;
  }

  /**
   * Get whenInvalid
   * @return whenInvalid
   */
  
  @Schema(name = "whenInvalid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("whenInvalid")
  public String getWhenInvalid() {
    return whenInvalid;
  }

  public void setWhenInvalid(String whenInvalid) {
    this.whenInvalid = whenInvalid;
  }

  public PromoBoxResponseDto whenMultipleRedeem(String whenMultipleRedeem) {
    this.whenMultipleRedeem = whenMultipleRedeem;
    return this;
  }

  /**
   * Get whenMultipleRedeem
   * @return whenMultipleRedeem
   */
  
  @Schema(name = "whenMultipleRedeem", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("whenMultipleRedeem")
  public String getWhenMultipleRedeem() {
    return whenMultipleRedeem;
  }

  public void setWhenMultipleRedeem(String whenMultipleRedeem) {
    this.whenMultipleRedeem = whenMultipleRedeem;
  }

  public PromoBoxResponseDto whenSuccess(String whenSuccess) {
    this.whenSuccess = whenSuccess;
    return this;
  }

  /**
   * Get whenSuccess
   * @return whenSuccess
   */
  
  @Schema(name = "whenSuccess", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("whenSuccess")
  public String getWhenSuccess() {
    return whenSuccess;
  }

  public void setWhenSuccess(String whenSuccess) {
    this.whenSuccess = whenSuccess;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromoBoxResponseDto promoBoxResponseDto = (PromoBoxResponseDto) o;
    return Objects.equals(this.button, promoBoxResponseDto.button) &&
        Objects.equals(this.title, promoBoxResponseDto.title) &&
        Objects.equals(this.whenEmpty, promoBoxResponseDto.whenEmpty) &&
        Objects.equals(this.whenInvalid, promoBoxResponseDto.whenInvalid) &&
        Objects.equals(this.whenMultipleRedeem, promoBoxResponseDto.whenMultipleRedeem) &&
        Objects.equals(this.whenSuccess, promoBoxResponseDto.whenSuccess);
  }

  @Override
  public int hashCode() {
    return Objects.hash(button, title, whenEmpty, whenInvalid, whenMultipleRedeem, whenSuccess);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromoBoxResponseDto {\n");
    sb.append("    button: ").append(toIndentedString(button)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    whenEmpty: ").append(toIndentedString(whenEmpty)).append("\n");
    sb.append("    whenInvalid: ").append(toIndentedString(whenInvalid)).append("\n");
    sb.append("    whenMultipleRedeem: ").append(toIndentedString(whenMultipleRedeem)).append("\n");
    sb.append("    whenSuccess: ").append(toIndentedString(whenSuccess)).append("\n");
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

