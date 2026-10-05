package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PostingRhythmType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Package code details applied to a rate plan.
 */

@Schema(name = "RatePackage", description = "Package code details applied to a rate plan.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RatePackage {

  private @Nullable String code;

  private @Nullable String description;

  private @Nullable PostingRhythmType postingRhythm;

  private @Nullable Integer quantity;

  public RatePackage code(String code) {
    this.code = code;
    return this;
  }

  /**
   * The code of the package element or package group associateded to the rate plan.
   * @return code
   */
  @Size(min = 0, max = 20) 
  @Schema(name = "code", example = "PC1", description = "The code of the package element or package group associateded to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public RatePackage description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the package element or package group associateded to the rate plan.
   * @return description
   */
  @Size(min = 0, max = 20) 
  @Schema(name = "description", example = "Package Code Des 1", description = "Description of the package element or package group associateded to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public RatePackage postingRhythm(PostingRhythmType postingRhythm) {
    this.postingRhythm = postingRhythm;
    return this;
  }

  /**
   * Get postingRhythm
   * @return postingRhythm
   */
  @Valid 
  @Schema(name = "postingRhythm", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postingRhythm")
  public PostingRhythmType getPostingRhythm() {
    return postingRhythm;
  }

  public void setPostingRhythm(PostingRhythmType postingRhythm) {
    this.postingRhythm = postingRhythm;
  }

  public RatePackage quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Quantity of the package associateded to the rate plan.
   * @return quantity
   */
  
  @Schema(name = "quantity", example = "1", description = "Quantity of the package associateded to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("quantity")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RatePackage ratePackage = (RatePackage) o;
    return Objects.equals(this.code, ratePackage.code) &&
        Objects.equals(this.description, ratePackage.description) &&
        Objects.equals(this.postingRhythm, ratePackage.postingRhythm) &&
        Objects.equals(this.quantity, ratePackage.quantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, description, postingRhythm, quantity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RatePackage {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    postingRhythm: ").append(toIndentedString(postingRhythm)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
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

