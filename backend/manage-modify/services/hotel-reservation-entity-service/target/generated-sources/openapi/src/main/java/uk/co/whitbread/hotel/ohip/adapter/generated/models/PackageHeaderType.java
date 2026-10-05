package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CheckInPrimaryDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PostingAttributes;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TransactionDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UsageDetails;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PackageHeaderType
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackageHeaderType {

  private @Nullable PostingAttributes postingAttributes;

  private @Nullable CheckInPrimaryDetails primaryDetails;

  private @Nullable TransactionDetails transactionDetails;

  private @Nullable UsageDetails usageDetails;

  public PackageHeaderType postingAttributes(PostingAttributes postingAttributes) {
    this.postingAttributes = postingAttributes;
    return this;
  }

  /**
   * Get postingAttributes
   * @return postingAttributes
   */
  @Valid 
  @Schema(name = "postingAttributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postingAttributes")
  public PostingAttributes getPostingAttributes() {
    return postingAttributes;
  }

  public void setPostingAttributes(PostingAttributes postingAttributes) {
    this.postingAttributes = postingAttributes;
  }

  public PackageHeaderType primaryDetails(CheckInPrimaryDetails primaryDetails) {
    this.primaryDetails = primaryDetails;
    return this;
  }

  /**
   * Get primaryDetails
   * @return primaryDetails
   */
  @Valid 
  @Schema(name = "primaryDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("primaryDetails")
  public CheckInPrimaryDetails getPrimaryDetails() {
    return primaryDetails;
  }

  public void setPrimaryDetails(CheckInPrimaryDetails primaryDetails) {
    this.primaryDetails = primaryDetails;
  }

  public PackageHeaderType transactionDetails(TransactionDetails transactionDetails) {
    this.transactionDetails = transactionDetails;
    return this;
  }

  /**
   * Get transactionDetails
   * @return transactionDetails
   */
  @Valid 
  @Schema(name = "transactionDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transactionDetails")
  public TransactionDetails getTransactionDetails() {
    return transactionDetails;
  }

  public void setTransactionDetails(TransactionDetails transactionDetails) {
    this.transactionDetails = transactionDetails;
  }

  public PackageHeaderType usageDetails(UsageDetails usageDetails) {
    this.usageDetails = usageDetails;
    return this;
  }

  /**
   * Get usageDetails
   * @return usageDetails
   */
  @Valid 
  @Schema(name = "usageDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("usageDetails")
  public UsageDetails getUsageDetails() {
    return usageDetails;
  }

  public void setUsageDetails(UsageDetails usageDetails) {
    this.usageDetails = usageDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackageHeaderType packageHeaderType = (PackageHeaderType) o;
    return Objects.equals(this.postingAttributes, packageHeaderType.postingAttributes) &&
        Objects.equals(this.primaryDetails, packageHeaderType.primaryDetails) &&
        Objects.equals(this.transactionDetails, packageHeaderType.transactionDetails) &&
        Objects.equals(this.usageDetails, packageHeaderType.usageDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(postingAttributes, primaryDetails, transactionDetails, usageDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackageHeaderType {\n");
    sb.append("    postingAttributes: ").append(toIndentedString(postingAttributes)).append("\n");
    sb.append("    primaryDetails: ").append(toIndentedString(primaryDetails)).append("\n");
    sb.append("    transactionDetails: ").append(toIndentedString(transactionDetails)).append("\n");
    sb.append("    usageDetails: ").append(toIndentedString(usageDetails)).append("\n");
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

