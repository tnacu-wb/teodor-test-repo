package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * BillingPrivilegesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BillingPrivilegesDto {

  private @Nullable Boolean directBillAuthorized;

  private @Nullable Boolean postingRestriction;

  private @Nullable Boolean videoCheckout;

  public BillingPrivilegesDto directBillAuthorized(Boolean directBillAuthorized) {
    this.directBillAuthorized = directBillAuthorized;
    return this;
  }

  /**
   * Get directBillAuthorized
   * @return directBillAuthorized
   */
  
  @Schema(name = "directBillAuthorized", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("directBillAuthorized")
  public Boolean getDirectBillAuthorized() {
    return directBillAuthorized;
  }

  public void setDirectBillAuthorized(Boolean directBillAuthorized) {
    this.directBillAuthorized = directBillAuthorized;
  }

  public BillingPrivilegesDto postingRestriction(Boolean postingRestriction) {
    this.postingRestriction = postingRestriction;
    return this;
  }

  /**
   * Get postingRestriction
   * @return postingRestriction
   */
  
  @Schema(name = "postingRestriction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postingRestriction")
  public Boolean getPostingRestriction() {
    return postingRestriction;
  }

  public void setPostingRestriction(Boolean postingRestriction) {
    this.postingRestriction = postingRestriction;
  }

  public BillingPrivilegesDto videoCheckout(Boolean videoCheckout) {
    this.videoCheckout = videoCheckout;
    return this;
  }

  /**
   * Get videoCheckout
   * @return videoCheckout
   */
  
  @Schema(name = "videoCheckout", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("videoCheckout")
  public Boolean getVideoCheckout() {
    return videoCheckout;
  }

  public void setVideoCheckout(Boolean videoCheckout) {
    this.videoCheckout = videoCheckout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BillingPrivilegesDto billingPrivilegesDto = (BillingPrivilegesDto) o;
    return Objects.equals(this.directBillAuthorized, billingPrivilegesDto.directBillAuthorized) &&
        Objects.equals(this.postingRestriction, billingPrivilegesDto.postingRestriction) &&
        Objects.equals(this.videoCheckout, billingPrivilegesDto.videoCheckout);
  }

  @Override
  public int hashCode() {
    return Objects.hash(directBillAuthorized, postingRestriction, videoCheckout);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BillingPrivilegesDto {\n");
    sb.append("    directBillAuthorized: ").append(toIndentedString(directBillAuthorized)).append("\n");
    sb.append("    postingRestriction: ").append(toIndentedString(postingRestriction)).append("\n");
    sb.append("    videoCheckout: ").append(toIndentedString(videoCheckout)).append("\n");
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

